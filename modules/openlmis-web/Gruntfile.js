/*
 * This program is part of the OpenLMIS logistics management information system platform software.
 * Copyright © 2013 VillageReach
 *
 * This program is free software: you can redistribute it and/or modify it under the terms of the GNU Affero General Public License as published by the Free Software Foundation, either version 3 of the License, or (at your option) any later version.
 *  
 * This program is distributed in the hope that it will be useful, but WITHOUT ANY WARRANTY; without even the implied warranty of MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the GNU Affero General Public License for more details.
 * You should have received a copy of the GNU Affero General Public License along with this program.  If not, see http://www.gnu.org/licenses.  For additional information contact info@OpenLMIS.org. 
 */

module.exports = function (grunt) {
    require('matchdep').filterDev('grunt-*').forEach(grunt.loadNpmTasks);

    grunt.initConfig({
      pkg: grunt.file.readJSON('package.json'),
      clean: ['dist', 'src/main/webapp/public/minJs/', 'quality'],
      jshint:{
        options: {
          undef: false,
          strict: false,
          '-W030' : true,
          '-W038' : true,
          unused: false,
          passfail: true,
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
            import:2,
            "box-model":false,
            "box-sizing": false,
            "bulletproof-font-face": false,
            "adjoining-classes":false
          },
          src:['src/main/webapp/public/css/*.css']
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
