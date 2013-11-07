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
    jshint: {
      options: {
        undef: false,
        strict: false,
        '-W030': true,
        unused: false,
        passfail: true,
        reporter: 'checkstyle',
        reporterOutput: 'quality/js/checkstyle-results.xml'
      },
      all: ['src/main/webapp/public/js/**/*.js']
    },
    lesslint: {
      src: ['src/main/webapp/public/less/*.less', '!**/ng-grid.less'],
      options: {
        quiet: true,
        formatters: [
          {id: 'checkstyle-xml', dest: 'quality/less/checkstyle-results.xml'}
        ],
        csslint: {
          ids: false,
          "bulletproof-font-face": false,
          "box-model": false,
          "box-sizing": false
        }
      }
    },
    less: {
      compile: {
        files: [
          {
            expand: true,
            cwd: "src/main/webapp/public/less/",
            src: ["**/*.less"],
            dest: "src/main/webapp/public/css/",
            ext: ".css",
            flatten: false
          }
        ]
      }
    },
    watch: {
      files: "src/main/webapp/public/less/*.less",
      tasks: ["less"],
      options: {
        spawn: false
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

  grunt.registerTask('default', ['clean', 'jshint', 'lesslint']);
};
