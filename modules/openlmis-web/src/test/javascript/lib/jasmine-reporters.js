/*global apiReporter: true, jasmine: false */
apiReporter = new jasmine.JsApiReporter();
jasmine.getEnv().addReporter(apiReporter);
jasmine.getEnv().addReporter(new jasmine.TrivialReporter());

(function() {

     var my$ = $.noConflict(true);
     var mylint = JSLINT;

     var checkLint = function() {
         if ( !mylint ) {
             return;
         }
         var sourceLocations = [];
         my$(".lintable").each( function( i, x ) { sourceLocations.push( x.src ); } );
         var jslDiv = my$("<div />", { id:"jslintContainer" } )
             .appendTo( my$("body") )
             .addClass( "jslintContainer" );
         jslDiv.append( my$("<div />", { id:"jslintBanner", text:"JSLint" } ) );
         my$.each( sourceLocations, function( i, x ) {
                       jslDiv.append( my$("<div />", {id: "jslint"  + i, 
                                                      text: "jslint for " + x + " is loading" } ));
                   } );
         var summaries = [];
         var anyErrors = false;
         my$.each( sourceLocations, 
                   function( i, x ) {
                       var proxy = { id:i, src:x, done:false, error:false };
                       summaries.push( proxy );
                       var resp = my$.ajax( x );
                       resp.error( function() { 
                                       anyErrors = true;
                                       proxy.error = true; 
                                       proxy.message = "Error retriving source "; //TODO be specific
                                       proxy.done = true;
                                       render( proxy ); 
                                   } );
                       resp.success( function() { 
                                         proxy.source = resp.responseText;
                                         proxy.done = true;
                                         try {
                                             // TODO get config by name from custom attribute
                                             proxy.jslOk = mylint( proxy.source, {} );
                                             proxy.jslReport = mylint.report();
                                             proxy.jslData = mylint.data();
                                             if ( countErrors( proxy.jslData ) > 0 ) {
                                                 anyErrors = true;
                                                 proxy.error = true;
                                                 proxy.message = "Found JSLint issues.";
                                             }
                                             else {
                                                 proxy.message = "JSLint OK";
                                             }
                                         }
                                         catch( e ) {
                                             anyErrors = true;
                                             proxy.error = true;
                                             proxy.message = "Error applying mylint: " + e ;
                                         }
                                         render( proxy );
                                     } );
                       var jslintResults = { summaries:summaries, anyErrors:anyErrors };
                       apiReporter.jslintResults = jslintResults;
                   });
         
         var sourceDisplay = function( path ) {
             var lastSlash = path.lastIndexOf( "/" );
             if ( path.length < 10 || lastSlash < 0 ) {
                 return path;
             }
             while ( path.length - lastSlash < 3 ) {
                 lastSlash = path.substring( 0, lastSlash - 3).lastIndexOf( "/" );
             }
             return "..." + path.substring( lastSlash + 1 );
         };
         
         var countErrors = function( jsd ) {
             var errCount = jsd.errors ? jsd.errors.length : 0;
             if ( jsd.implieds && jsd.implieds.length ) {
                 ++errCount;
             }
             if ( jsd.unused && jsd.unused.length ) {
                 ++errCount;
             }
             return errCount;             
         };
         
         var render = function( proxy ) {
             var jsli = my$("#jslint" + proxy.id).empty();
             var headline = my$("<div />")
                 .addClass( "jslint" )
                 .appendTo( jsli );
             var lintData = proxy.jslData;
             var sourceFileName = sourceDisplay( proxy.src );
             if ( ! lintData ) {
                 headline.append( sourceFileName + ": No JSLint Data?" ).addClass( "jslintfailed" );
             }
             else {
                 var errCount = countErrors( lintData );
                 var hasErrors = errCount > 0;
                 headline.addClass( hasErrors ? "jslintfailed" : "jslintpassed" );
                 var detail = my$("<div />", {
                                      id:"jslintDetail" + proxy.id
                                  } ).addClass( "hidden" );
                 var hltext = sourceFileName + ": " + (hasErrors ? errCount :"No") + " issues";
                 my$("<a/>", {text:hltext, title:proxy.src, href:"#"} )
                     .click( function( event ) {
                                 detail.toggleClass( "hidden" );
                             } )
                     .appendTo( headline );
                 detail.appendTo( headline );
                 if ( lintData.implieds && lintData.implieds.length ) {
                     var msgs = my$( "<p />" ).appendTo( detail );
                     msgs.append( "Implied globals: " ); 
                     my$.each( lintData.implieds, function( i, x ) {
                                   msgs.append( my$("<span />", {text:x.name, title: "line " + x.line} )).append( "; " );
                               } );
                 }
                 if ( lintData.unused && lintData.unused.length ) {
                   var msgs = my$( "<p />" ).appendTo( detail );
                   msgs.append( "Unused: " ); 
                   my$.each( lintData.unused, function( i, x ) {
                               msgs.append( my$("<span />", {text:x.name, title: "line " + x.line } )).append( "; " );
                             } );
                     detail.append( my$("<p />", 
                                        { text:msg, title: "line " + x.line } ));
                 }
                 if ( lintData.errors ) {
                     my$.each( lintData.errors, function( i, x ) {
                                   var fullEvidence = "(none)";
                                   var truncEvidence = "(none)";
                                   var lineMessage = "Nothing from jslint!?!?;";
                                   if ( x ) {
                                       if ( x.evidence) {
                                           fullEvidence = x.evidence;
                                           truncEvidence = x.evidence.length > 80 ? x.evidence.substring( 0, 80 ) + "..." : x.evidence;
                                       }
                                       lineMessage = "Line " + x.line + ": " + x.reason 
                                           + " (" + truncEvidence + ")";
                                       detail.append( my$("<p />", 
                                                          {text:lineMessage, 
                                                           title: fullEvidence} )
                                                    );
                                   }
                               } );
                 }
             }
         };
             
     };
     var jslintListener = new jasmine.Reporter();
     jslintListener.reportRunnerResults = function( x ) {
         checkLint(); 
     };
     jasmine.getEnv().addReporter( jslintListener );
     
    
})();
