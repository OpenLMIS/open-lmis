module.exports = function (grunt) {
    require('matchdep').filterDev('grunt-*').forEach(grunt.loadNpmTasks);

    grunt.initConfig({
      pkg: grunt.file.readJSON('package.json'),
      clean: ['dist', 'src/main/webapp/public/minJs/', 'quality'],
      jshint:{
        options: {
          reporter: 'checkstyle',
          reporterOutput: 'quality/js/report.xml'
        },
        all:['src/main/webapp/public/js/**/*.js']
      },
      csslint:{
        options: {
          absoluteFilePathsForFormatters: true,
          quiet: true,
          formatters: [
            {id: 'compact', dest: 'quality/css/report.txt'}
          ]
        },
        strict:{
          options:{
            force: true,
            import:2,
            "box-model":false,
            "box-sizing": false,
            "bulletproof-font-face": false,
            "adjoining-classes":false
          },
          src:['src/main/webapp/public/css/*.css'],
        },
        lax: {
          options: {
            import: false
          },
          src: ['src/main/webapp/public/css/ng-grid.css']
        }
      },
      uglify: {
        options: {
          mangle: false,
          beautify: true,
          report: 'min',
          preserveComments: false
        },
        files: {
          cwd: 'src/main/webapp/public/js/',
          src: ['**/*.js'],
          dest: 'src/main/webapp/public/minJs/',
          expand: true,
          flatten: false
        }
      }
    });

    grunt.registerTask('default', ['clean', 'jshint', 'csslint']);
};
