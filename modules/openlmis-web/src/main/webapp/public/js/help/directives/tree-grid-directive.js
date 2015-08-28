/*
 * Electronic Logistics Management Information System (eLMIS) is a supply chain management system for health commodities in a developing country setting.
 *
 * Copyright (C) 2015  John Snow, Inc (JSI). This program was produced for the U.S. Agency for International Development (USAID). It was prepared under the USAID | DELIVER PROJECT, Task Order 4.
 *
 * This program is free software: you can redistribute it and/or modify it under the terms of the GNU Affero General Public License as published by the Free Software Foundation, either version 3 of the License, or (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful, but WITHOUT ANY WARRANTY; without even the implied warranty of MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the GNU Affero General Public License for more details.
 *
 * You should have received a copy of the GNU Affero General Public License along with this program.  If not, see <http://www.gnu.org/licenses/>.
 */
app.directive('treeGrid', function ($timeout) {

        return {
            restrict: 'E',
            //templateUrl:'tree-grid-template.html',
            //template:"<div><table class=\"table table-bordered table-striped tree-grid\"><thead class=\"text-primary\"><tr><th>{{expandingProperty}}</th><th ng-repeat=\"col in colDefinitions\">{{col.displayName || col.field}}</th></tr></thead><tbody><tr ng-repeat=\"row in tree_rows | filter:{visible:true} track by row.branch.uid\" ng-class=\"'level-' + {{ row.level }} + (row.branch.selected ? ' active':'')\" class=\"tree-grid-row\"><td class=\"text-primary\"><a ng-click=\"user_clicks_branch(row.branch)\"><i ng-class=\"row.tree_icon\" ng-click=\"row.branch.expanded = !row.branch.expanded\" class=\"indented tree-icon\"></i></a><span class=\"indented tree-label\">{{row.branch[expandingProperty]}}</span></td><td ng-repeat=\"col in colDefinitions\">{{row.branch[col.field]}}</td></tr></tbody><table></div>",
            template: "<div>" +
                " <table class=\"table table-bordered table-striped tree-grid\">" +
                "   <thead class=\"text-primary\">" +
                "  <tr>" +
                "     <th>{{expandingProperty}}</th>" +
                "     <th ng-repeat=\"col in colDefinitions\">{{col.displayName || col.field}}</th>" +
                " </tr>" +
                "   </thead>" +
                "  <tbody>  <tr ng-repeat=\"row in tree_rows | filter:{visible:true} track by row.branch.uid\"  ng-class=\"'level-'+ {{ row.level }} + (row.branch.selected ? 'active ':'')\" class=\"tree-grid-row\">" +
                "   <td   ng-class=\"row.branch.category ? 'text-primary':'tree-link-a'\"><a ng-click=\"user_clicks_branch(row.branch)\"><i ng-class=\"row.tree_icon\"   ng-click=\"row.branch.expanded = !row.branch.expanded\"" +
                "     class=\" indented tree-icon\"></i>" +
                "    </a><span class=\"indented tree-label\" ng-click=\"user_clicks_branch(row.branch)\">" +
                "       {{row.branch[expandingProperty]}}</span> " +
                "    </td> " +
                "        <td ng-repeat=\"col in colDefinitions\">{{row.branch[col.field]}}</td>" +
                "        <tr >" +
                "   </tbody>" +
                "    </table>" +
                "  </div>",
            replace: true,
            scope: {
                treeData: '=',
                colDefs: '=',
                expandOn: '=',
                onSelect: '&',
                initialSelection: '@',
                treeControl: '='
            },
            link: function (scope, element, attrs) {
                var error, expandingProperty, expand_all_parents, expand_level, for_all_ancestors, for_each_branch, get_parent, n, on_treeData_change, select_branch, selected_branch, tree;

//                    error = function (s) {
//                    console.log('ERROR:' + s);
//                    debugger;
//                    return void 0;
//                    };

                if (!attrs.iconExpand ) {
                    attrs.iconExpand = 'icon-plus  glyphicon glyphicon-plus  fa fa-plus';
                }
                if (!attrs.iconCollapse) {
                    attrs.iconCollapse = 'icon-minus glyphicon glyphicon-minus fa fa-minus';
                }
                if (!attrs.iconLeaf) {
                    attrs.iconLeaf = 'icon-file  glyphicon glyphicon-file  fa fa-file';
                }
                if (!attrs.expandLevel ) {
                    attrs.expandLevel = '3';
                }

                expand_level = parseInt(attrs.expandLevel, 10);

                if (!scope.treeData) {
                    ////alert('no treeData defined for the tree!');
                    return;
                }
                if (scope.treeData.length === null) {
                    if (treeData.label) {
                        scope.treeData = [treeData];
                    } else {
                        ////alert('treeData should be an array of root branches');
                        return;
                    }
                }
                if (attrs.expandOn) {
                    expandingProperty = scope.expandOn;
                    scope.expandingProperty = scope.expandOn;
                }
                else {
                    var _firstRow = scope.treeData[0],
                        _keys = Object.keys(_firstRow);
                    for (var i = 0, len = _keys.length; i < len; i++) {
                        if (typeof(_firstRow[_keys[i]]) === 'string') {
                            expandingProperty = _keys[i];
                            break;
                        }
                    }
                    if (!expandingProperty) expandingProperty = _keys[0];
                    scope.expandingProperty = expandingProperty;
                }

                if (!attrs.colDefs) {
                    var _col_defs = [],
                    _firstRow1 = scope.treeData[0], _unwantedColumn = ['children', 'level', 'expanded', expandingProperty];
                    for (var idx in _firstRow1) {
                        if (_unwantedColumn.indexOf(idx) === -1)
                            _col_defs.push({field: idx});
                    }
                    scope.colDefinitions = _col_defs;
                }
                else {
                    console.log(scope.colDefs);
                    scope.colDefinitions = scope.colDefs;
                }

                for_each_branch = function (f) {
                    var do_f, root_branch, _i, _len, _ref, _results;
                    do_f = function (branch, level) {
                        var child, _i, _len, _ref, _results;
                        f(branch, level);
                        if (branch.children) {
                            _ref = branch.children;
                            _results = [];
                            for (_i = 0, _len = _ref.length; _i < _len; _i++) {
                                child = _ref[_i];
                                _results.push(do_f(child, level + 1));
                            }
                            return _results;
                        }
                    };
                    _ref = scope.treeData;
                    _results = [];
                    for (_i = 0, _len = _ref.length; _i < _len; _i++) {
                        root_branch = _ref[_i];
                        _results.push(do_f(root_branch, 1));
                    }
                    return _results;
                };
                selected_branch = null;
                select_branch = function (branch) {
                    if (!branch) {
                        if (selected_branch ) {
                            selected_branch.selected = false;
                        }
                        selected_branch = null;
                        return;
                    }
                    if (branch !== selected_branch) {
                        if (selected_branch) {
                            selected_branch.selected = false;
                        }
                        branch.selected = true;
                        selected_branch = branch;
                        expand_all_parents(branch);
                        if (branch.onSelect ) {
                            return $timeout(function () {
                                return branch.onSelect(branch);
                            });
                        } else {
                            if (scope.onSelect) {
                                return $timeout(function () {
                                    return scope.onSelect({
                                        branch: branch
                                    });
                                });
                            }
                        }
                    }
                };
                scope.user_clicks_branch = function (branch) {
                    if (branch !== selected_branch) {
                        return select_branch(branch);
                    }
                };
                get_parent = function (child) {
                    var parent;
                    parent = void 0;
                    if (child.parent_uid) {
                        for_each_branch(function (b) {
                            if (b.uid === child.parent_uid) {
                                parent = b;
                                return parent;
                            }
                        });
                    }
                    return parent;
                };
                for_all_ancestors = function (child, fn) {
                    var parent;
                    parent = get_parent(child);
                    if (parent ) {
                        fn(parent);
                        return for_all_ancestors(parent, fn);
                    }
                };
                expand_all_parents = function (child) {
                    return for_all_ancestors(child, function (b) {
                        b.expanded = true;
                        return b.expanded ;
                    });
                };

                scope.tree_rows = [];

                on_treeData_change = function () {
                    var add_branch_to_list, root_branch, _i, _len, _ref, _results;
                    for_each_branch(function (b, level) {
                        if (!b.uid) {
                            b.uid = "" + Math.random();
                            return b.uid;
                        }
                    });
                    for_each_branch(function (b) {
                        var child, _i, _len, _ref, _results;
                        if (angular.isArray(b.children)) {
                            _ref = b.children;
                            _results = [];
                            for (_i = 0, _len = _ref.length; _i < _len; _i++) {
                                child = _ref[_i];
                                _results.push(child.parent_uid = b.uid);
                            }
                            return _results;
                        }
                    });
                    scope.tree_rows = [];
                    for_each_branch(function (branch) {
                        var child, f;
                        if (branch.children) {
                            if (branch.children.length > 0) {
                                f = function (e) {
                                    if (typeof e === 'string') {
                                        return {
                                            label: e,
                                            children: []
                                        };
                                    } else {
                                        return e;
                                    }
                                };
                                branch.children = (function () {
                                    var _i, _len, _ref, _results;
                                    _ref = branch.children;
                                    _results = [];
                                    for (_i = 0, _len = _ref.length; _i < _len; _i++) {
                                        child = _ref[_i];
                                        _results.push(f(child));
                                    }
                                    return _results;
                                })();
                                return branch.children;
                            }
                        } else {
                            branch.children = [];
                            return branch.children;
                        }
                    });
                    add_branch_to_list = function (level, branch, visible) {
                        var child, child_visible, tree_icon, _i, _len, _ref, _results;
                        if (branch.expanded === null) {
                            branch.expanded = false;
                        }
                        if (!branch.children || branch.children.length === 0) {
                            tree_icon = attrs.iconLeaf;
                        } else {
                            if (branch.expanded) {
                                tree_icon = attrs.iconCollapse;
                            } else {
                                tree_icon = attrs.iconExpand;
                            }
                        }
                        branch.level = level;
                        scope.tree_rows.push({
                            level: level,
                            branch: branch,
                            label: branch[expandingProperty],
                            tree_icon: tree_icon,
                            visible: visible
                        });
                        if (branch.children ) {
                            _ref = branch.children;
                            _results = [];
                            for (_i = 0, _len = _ref.length; _i < _len; _i++) {
                                child = _ref[_i];
                                child_visible = visible && branch.expanded;
                                _results.push(add_branch_to_list(level + 1, child, child_visible));
                            }
                            return _results;
                        }
                    };
                    _ref = scope.treeData;
                    _results = [];
                    for (_i = 0, _len = _ref.length; _i < _len; _i++) {
                        root_branch = _ref[_i];
                        _results.push(add_branch_to_list(1, root_branch, true));
                    }
                    return _results;
                };

                scope.$watch('treeData', on_treeData_change, true);

                if (attrs.initialSelection ) {
                    for_each_branch(function (b) {
                        if (b.label === attrs.initialSelection) {
                            return $timeout(function () {
                                return select_branch(b);
                            });
                        }
                    });
                }
                n = scope.treeData.length;
                for_each_branch(function (b, level) {
                    b.level = level;
                    b.expanded = b.level < expand_level;
                    return b.expanded ;
                });
                if (scope.treeControl ) {
                    if (angular.isObject(scope.treeControl)) {
                        tree = scope.treeControl;
                        tree.expand_all = function () {
                            return for_each_branch(function (b, level) {
                                b.expanded = true;
                                return b.expanded ;
                            });
                        };
                        tree.collapse_all = function () {
                            return for_each_branch(function (b, level) {
                                b.expanded = false;
                                return b.expanded ;
                            });
                        };
                        tree.get_first_branch = function () {
                            n = scope.treeData.length;
                            if (n > 0) {
                                return scope.treeData[0];
                            }
                        };
                        tree.select_first_branch = function () {
                            var b;
                            b = tree.get_first_branch();
                            return tree.select_branch(b);
                        };
                        tree.get_selected_branch = function () {
                            return selected_branch;
                        };
                        tree.get_parent_branch = function (b) {
                            return get_parent(b);
                        };
                        tree.select_branch = function (b) {
                            select_branch(b);
                            return b;
                        };
                        tree.get_children = function (b) {
                            return b.children;
                        };
                        tree.select_parent_branch = function (b) {
                            var p;
                            if (b === null) {
                                b = tree.get_selected_branch();
                            }
                            if (b ) {
                                p = tree.get_parent_branch(b);
                                if (p ) {
                                    tree.select_branch(p);
                                    return p;
                                }
                            }
                        };
                        tree.add_branch = function (parent, new_branch) {
                            if (parent ) {
                                parent.children.push(new_branch);
                                parent.expanded = true;
                            } else {
                                scope.treeData.push(new_branch);
                            }
                            return new_branch;
                        };
                        tree.add_root_branch = function (new_branch) {
                            tree.add_branch(null, new_branch);
                            return new_branch;
                        };
                        tree.expand_branch = function (b) {
                            if (b === null) {
                                b = tree.get_selected_branch();
                            }
                            if (b ) {
                                b.expanded = true;
                                return b;
                            }
                        };
                        tree.collapse_branch = function (b) {
                            if (b === null) {
                                b = selected_branch;
                            }
                            if (b ) {
                                b.expanded = false;
                                return b;
                            }
                        };
                        tree.get_siblings = function (b) {
                            var p, siblings;
                            if (b === null) {
                                b = selected_branch;
                            }
                            if (b ) {
                                p = tree.get_parent_branch(b);
                                if (p) {
                                    siblings = p.children;
                                } else {
                                    siblings = scope.treeData;
                                }
                                return siblings;
                            }
                        };
                        tree.get_next_sibling = function (b) {
                            var i, siblings;
                            if (b === null) {
                                b = selected_branch;
                            }
                            if (b ) {
                                siblings = tree.get_siblings(b);
                                n = siblings.length;
                                i = siblings.indexOf(b);
                                if (i < n) {
                                    return siblings[i + 1];
                                }
                            }
                        };
                        tree.get_prev_sibling = function (b) {
                            var i, siblings;
                            if (b === null) {
                                b = selected_branch;
                            }
                            siblings = tree.get_siblings(b);
                            n = siblings.length;
                            i = siblings.indexOf(b);
                            if (i > 0) {
                                return siblings[i - 1];
                            }
                        };
                        tree.select_next_sibling = function (b) {
                            var next;
                            if (b === null) {
                                b = selected_branch;
                            }
                            if (b ) {
                                next = tree.get_next_sibling(b);
                                if (next ) {
                                    return tree.select_branch(next);
                                }
                            }
                        };
                        tree.select_prev_sibling = function (b) {
                            var prev;
                            if (b === null) {
                                b = selected_branch;
                            }
                            if (b ) {
                                prev = tree.get_prev_sibling(b);
                                if (prev ) {
                                    return tree.select_branch(prev);
                                }
                            }
                        };
                        tree.get_first_child = function (b) {
                            var _ref;
                            if (b === null) {
                                b = selected_branch;
                            }
                            if (b ) {
                                if (((_ref = b.children)  ? _ref.length : void 0) > 0) {
                                    return b.children[0];
                                }
                            }
                        };
                        tree.get_closest_ancestor_next_sibling = function (b) {
                            var next, parent;
                            next = tree.get_next_sibling(b);
                            if (next ) {
                                return next;
                            } else {
                                parent = tree.get_parent_branch(b);
                                return tree.get_closest_ancestor_next_sibling(parent);
                            }
                        };
                        tree.get_next_branch = function (b) {
                            var next;
                            if (b === null) {
                                b = selected_branch;
                            }
                            if (b ) {
                                next = tree.get_first_child(b);
                                if (next ) {
                                    return next;
                                } else {
                                    next = tree.get_closest_ancestor_next_sibling(b);
                                    return next;
                                }
                            }
                        };
                        tree.select_next_branch = function (b) {
                            var next;
                            if (b === null) {
                                b = selected_branch;
                            }
                            if (b ) {
                                next = tree.get_next_branch(b);
                                if (next ) {
                                    tree.select_branch(next);
                                    return next;
                                }
                            }
                        };
                        tree.last_descendant = function (b) {
                            var last_child;
                            if (b === null) {
                                "";
                            }
                            n = b.children.length;
                            if (n === 0) {
                                return b;
                            } else {
                                last_child = b.children[n - 1];
                                return tree.last_descendant(last_child);
                            }
                        };
                        tree.get_prev_branch = function (b) {
                            var parent, prev_sibling;
                            if (b === null) {
                                b = selected_branch;
                            }
                            if (b ) {
                                prev_sibling = tree.get_prev_sibling(b);
                                if (prev_sibling ) {
                                    return tree.last_descendant(prev_sibling);
                                } else {
                                    parent = tree.get_parent_branch(b);
                                    return parent;
                                }
                            }
                        };
                         tree.select_prev_branch = function (b) {
                            var prev;
                            if (b === null) {
                                b = selected_branch;
                            }
                            if (b ) {
                                prev = tree.get_prev_branch(b);
                                if (prev ) {
                                    tree.select_branch(prev);
                                    return prev;
                                }
                            }
                        };
                        return tree.select_prev_branch;
                    }
                }
            }
        };
    }
);
