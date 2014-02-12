/**
 * Created with IntelliJ IDEA.
 * User: issa
 * Date: 2/11/14
 * Time: 10:53 PM
 * To change this template use File | Settings | File Templates.
 */

function createTab(tabId){
    var tabNum = tabId.substr(tabId.length - 1);
    var contentId = tabId +'-'+ tabNum;

    if($('#'+tabId).length == 0){ //tab does not exist
        $('.nav-tabs').prepend('<li><a id="'+tabId+'" href="#' + contentId + '" data-toggle="tab"'+'><button class="close closeTab" type="button" >×</button>Tab '+tabNum +'</a></li>');
        showTab(tabId);

        registerCloseEvent();
    }else{
        showTab(tabId);
    }
}

function registerCloseEvent() {

    $('#dashboard-tabs').on('click', ' li a .close', function() {
       // var tabId = $(this).parents('li').children('a').attr('href');
        $(this).parents('li').remove('li');
        //$(tabId).remove();  do not remove tab-content
        $('#dashboard-tabs a:first').tab('show');
    });

}

function showTab(tabId) {
    $('#dashboard-tabs a[id="' + tabId + '"]').tab('show');
}
