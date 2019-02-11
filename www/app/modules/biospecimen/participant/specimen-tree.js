
angular.module('os.biospecimen.participant.specimen-tree', 
  [
    'os.biospecimen.models', 
    'os.biospecimen.participant.collect-specimens',
  ])
  .directive('osSpecimenTree', function(
    $state, $stateParams, $modal, $timeout, $rootScope, $q, $injector,
    CpConfigSvc, CollectSpecimensSvc, Visit, Specimen, SpecimenLabelPrinter, SpecimensHolder,
    ExtensionsUtil, DistributionOrder, DistributionProtocol, Alerts, SpecimenUtil) {

    var allowedDps = undefined;

    function openSpecimenTree(specimens, openedNodesMap, treeCfg) {
      var defExpandDepth = treeCfg.defaultExpandDepth;
      if (defExpandDepth == undefined || defExpandDepth == null || isNaN(defExpandDepth)) {
        defExpandDepth = 0;
      } else {
        defExpandDepth = +defExpandDepth - 1;
      }

      angular.forEach(specimens,
        function(specimen) {
          if (openedNodesMap) {
            var key = (specimen.id || 'u') + '-' + (specimen.reqId || 'u');
            specimen.isOpened = openedNodesMap[key];
          } else if (defExpandDepth < -1 || specimen.depth <= defExpandDepth) {
            specimen.isOpened = true;
          }
        }
      );
    }

    function toggleAllSelected(selection, specimens, specimen) {
      if (!specimen.selected) {
        selection.all = false;
        return;
      }

      for (var i = 0; i < specimens.length; ++i) {
        if (!specimens[i].selected) {
          selection.all = false;
          return;
        }
      }

      selection.all = true;
    };

    function selectParentSpecimen(specimen) {
      if (!specimen.selected) {
        return false;
      }

      var parent = specimen.parent;
      while (parent) {
        parent.selected = true;
        parent = parent.parent;
      }
    };

    function selectDescendants(specimen) {
      angular.forEach(specimen.children,
        function(childSpmn) {
          childSpmn.selected = specimen.selected;
          selectDescendants(childSpmn);
        }
      );
    }

    function isAnySelected(specimens) {
      for (var i = 0; i < specimens.length; ++i) {
        if (specimens[i].selected) {
          return true;
        }
      }

      return false;
    }

    function isAnyPendingSelected(specimens) {
      for (var i = 0; i < specimens.length; ++i) {
        if (specimens[i].selected && specimens[i].status != 'Collected') {
          return true;
        }
      }

      return false;
    }

    function isAnyPendingDescendantSelected(specimen) {
      if (!!specimen.specimensPool) {
        for (var i = 0; i < specimen.specimensPool.length; ++i) {
          if (specimen.specimensPool[i].selected && specimen.specimensPool[i].status != 'Collected') {
            return true;
          }
        }
      }

      if (!specimen.children) {
        return false;
      }

      for (var i = 0; i < specimen.children.length; ++i) {
        if (specimen.children[i].selected && specimen.children[i].status != 'Collected') {
          return true;
        }

        if (isAnyPendingDescendantSelected(specimen.children[i])) {
          return true;
        }
      }

      return false;
    };

    function getState() {
      return {state: $state.current, params: $stateParams};
    };

    function showSelectSpecimens(msgCode) {
      if (!msgCode) {
        return;
      }

      Alerts.error(msgCode);
    };

    function getSelectedSpecimens (scope, message, anyStatus) {
      if (!scope.selection.any) {
        showSelectSpecimens(message);
        return [];
      }

      var specimens = [];
      angular.forEach(scope.specimens, function(specimen) {
        if (!specimen.selected) {
          return;
        }

        if ((specimen.status == 'Collected' || anyStatus) && specimen.id) {
          specimens.push(specimen);
        }
      });

      if (specimens.length == 0) {
        showSelectSpecimens(message);
      }

      return specimens;
    };

    function isOldVisit(visitDate, interval) {
      if (!visitDate && visitDate != 0) {
        return false;
      }

      var dispCutOff = new Date(visitDate);
      dispCutOff.setDate(dispCutOff.getDate() + interval);
      return dispCutOff.getTime() < Date.now();
    }

    function hideOldPendingSpmns(specimens, interval) {
      var result = true, visitsMap = {};

      angular.forEach(specimens,
        function(specimen) {
          if (!specimen.visitId) {
            result = false;
            return;
          }

          if (!!specimen.status && specimen.status != 'Pending') {
            return;
          }

          if (!visitsMap.hasOwnProperty(specimen.visitId)) {
            visitsMap[specimen.visitId] = isOldVisit(specimen.visitDate, interval);
          }

          specimen.$$hideN = visitsMap[specimen.visitId];
          if (result && (!specimen.status || specimen.status == 'Pending')) {
            result = specimen.$$hideN;
          }
        }
      );

      // result is false if at least one pending specimen is displayed.
      return result;
    }

    function toggleShowHidePendingSpmns(specimens, hide) {
      angular.forEach(specimens,
        function(specimen) {
          if (!specimen.status || specimen.status == 'Pending') {
            specimen.$$hideN = hide;
          }
        }
      );
    }

    function onlyPendingSpmns(specimens) {
      return (specimens || []).every(
        function(spmn) {
          return !spmn.status || spmn.status == 'Pending';
        }
      );
    }

    function anyPendingSpmnsInTree(specimens) {
      return (specimens || []).some(
        function(spmn) {
          if (!spmn.status || spmn.status == 'Pending') {
            return true;
          }

          if (!!spmn.children && anyPendingSpmnsInTree(spmn.children)) {
            return true;
          }

          if (!!spmn.specimensPool && anyPendingSpmnsInTree(spmn.specimensPool)) {
            return true;
          }
        }
      );
    }

    function initSdeTreeFields(scope, cpDict, treeCfg) {
      var fieldsSvc = $injector.get('sdeFieldsSvc');

      angular.forEach(scope.specimens,
        function(spmn) {
          ExtensionsUtil.createExtensionFieldMap(spmn);
        }
      );

      scope.dispTree = true;
      var fields = treeCfg.fields || [];
      scope.fields = fieldsSvc.commonFns().overrideFields(cpDict, fields);
      if (fields.length == 0) {
        return;
      }

      angular.forEach(scope.specimens,
        function(specimen) {
          var obj = {cpr: scope.cpr, specimen: specimen};
          specimen.$$treeFields = fields.map(
            function(field) {
              var result = {type: field.type, value: undefined};
              if (field.type == 'specimen-desc') {
                return result;
              }

              $q.when(fieldsSvc.commonFns().getValue({field: field}, obj)).then(
                function(value) {
                  result.value = value;
                }
              );

              return result;
            }
          );
        }
      );

      scope.hasDict = true;
    }

    function linker(scope, element, attrs, cpDict, treeCfg) {
      scope.title = attrs.title || 'specimens.list';
      scope.hasDict = false;
      scope.dispTree = false;
      scope.fields = [];

      scope.view = 'list';
      scope.parentSpecimen = undefined;

      scope.onlyPendingSpmns = onlyPendingSpmns(scope.specimenTree);
      scope.anyPendingSpmns  = anyPendingSpmnsInTree(scope.specimenTree);

      scope.specimens = Specimen.flatten(scope.specimenTree);
      openSpecimenTree(scope.specimens, null, treeCfg);
      scope.hidePendingSpmns = hideOldPendingSpmns(scope.specimens, scope.pendingSpmnsDispInterval);

      if ($injector.has('sdeFieldsSvc')) {
        initSdeTreeFields(scope, cpDict, treeCfg);
      } else {
        scope.dispTree = true;
      }

      scope.openSpecimenNode = function(specimen) {
        specimen.isOpened = true;
      };

      scope.closeSpecimenNode = function(specimen) {
        specimen.isOpened = false;
      };

      scope.selection = {all: false, any: false, anyPending: false};
      scope.toggleAllSpecimenSelect = function() {
        angular.forEach(scope.specimens, function(specimen) {
          specimen.selected = scope.selection.all;
        });

        var anySelected = scope.selection.any = scope.selection.all;
        scope.selection.anyPending = anySelected && isAnyPendingSelected(scope.specimens);
      };

      scope.toggleSpecimenSelect = function(specimen) {
        if (specimen.status != 'Collected') {
          selectParentSpecimen(specimen);
        }

        if (!specimen.isOpened) {
          selectDescendants(specimen);
        }

        toggleAllSelected(scope.selection, scope.specimens, specimen);

        var anySelected = scope.selection.any = specimen.selected ? true : isAnySelected(scope.specimens);
        scope.selection.anyPending = anySelected && isAnyPendingSelected(scope.specimens);
      };

      scope.collectSpecimens = function() {
        if (!scope.selection.anyPending) {
          showSelectSpecimens('specimens.no_specimens_for_collection');
          return;
        }

        var specimensToCollect = [];
        angular.forEach(scope.specimens, function(specimen) {
          if (specimen.selected && specimen.status != 'Collected') {
            specimen.isOpened = true;
            specimensToCollect.push(specimen);
          } else if (isAnyPendingDescendantSelected(specimen)) {
            if (specimen.status != 'Collected') {
              // a parent needs to be collected first
              specimen.selected = true;
            }
            specimen.isOpened = true;
            specimensToCollect.push(specimen);
          }
        });

        var onlyCollected = true;
        for (var i = 0; i < specimensToCollect.length; ++i) {
          if (specimensToCollect[i].status != 'Collected') {
            onlyCollected = false;
            break;
          }
        }

        if (onlyCollected) {
          showSelectSpecimens('specimens.no_specimens_for_collection');
          return;
        }

        var visit = scope.visit;
        if (!visit) {
          var eventId = undefined, visitId = undefined, error = false;
          for (var i = 0; i < specimensToCollect.length; ++i) {
            if (i == 0) {
              eventId = specimensToCollect[i].eventId;
              visitId = specimensToCollect[i].visitId;
            } else if (eventId != specimensToCollect[i].eventId || visitId != specimensToCollect[i].visitId) {
              error = true;
              break;
            }
          }

          if (error) {
            Alerts.error('specimens.errors.select_same_visit_spmns');
            return;
          }

          visit = new Visit({id: visitId, eventId: eventId, cpId: scope.cp.id});
        }

        visit.cprId = (scope.cpr && scope.cpr.id) || visit.cprId;
        CollectSpecimensSvc.collect(getState(), visit, specimensToCollect);
      };

      scope.addSpecimensToSpecimenList = function(list) {
        if (!scope.selection.any) {
          showSelectSpecimens('specimens.no_specimens_for_specimen_list');
          return;
        }
        var selectedSpecimens = [];
        getSelectedSpecimens(scope, 'specimens.no_specimens_for_specimen_list', true).map(
          function(specimen) {
            selectedSpecimens.push({id: specimen.id});
          }
        );

        if (selectedSpecimens.length == 0) {
          return;
        }

        if (!!list) {
          list.addSpecimens(selectedSpecimens).then(
            function(specimens) {
              var listType = list.getListType($rootScope.currentUser);
              Alerts.success('specimen_list.specimens_added_to_' + listType , list);
            }
          )
        } else {
          SpecimensHolder.setSpecimens(selectedSpecimens);
          $state.go('specimen-list-addedit', {listId: ''});
        }
      }

      scope.loadSpecimenTypes = function(specimenClass, notClear) {
        SpecimenUtil.loadSpecimenTypes(scope, specimenClass, notClear);
      };

      scope.showCloseSpecimen = function(specimen) {
        scope.view = 'close_specimen';
        scope.specStatus = { reason: '' };
        scope.parentSpecimen = specimen;
      };

      scope.closeSpecimen = function() {
        scope.parentSpecimen.close(scope.specStatus.reason).then(
          function() {
            scope.revertEdit();
          }
        );
      };
       
      scope.revertEdit = function() {
        scope.view = 'list';
        scope.parentSpecimen = undefined;
      }

      scope.toggleHidePendingSpmns = function() {
        scope.hidePendingSpmns = !scope.hidePendingSpmns;
        toggleShowHidePendingSpmns(scope.specimens, scope.hidePendingSpmns);
      }

      scope.getSelectedSpecimens = function(anyStatus) {
        return getSelectedSpecimens(scope, '', anyStatus);
      }

      scope.initTree = function() {
        if (!scope.reload) {
          return;
        }

        var openedNodesMap = {};
        angular.forEach(scope.specimens,
          function(spmn) {
            openedNodesMap[(spmn.id || 'u') + '-' + (spmn.reqId || 'u')] = spmn.isOpened;
          }
        );

        scope.reload().then(
          function() {
            $timeout(function() {
              scope.specimens = Specimen.flatten(scope.specimenTree);
              openSpecimenTree(scope.specimens, openedNodesMap, treeCfg);
              if ($injector.has('sdeFieldsSvc')) {
                initSdeTreeFields(scope, cpDict, treeCfg);
              }
            });
          }
        );
      }
    }

    return {
      restrict: 'E',

      scope: {
        cp: '=',
        cpr: '=',
        visit: '=',
        specimenTree: '=specimens',
        allowedOps: '=',
        reload: '&reload',
        pendingSpmnsDispInterval: '=?'
      },

      replace: true,

      templateUrl: 'modules/biospecimen/participant/specimens.html',

      link: function(scope, element, attrs) {
        var cpDictQ  = CpConfigSvc.getDictionary(scope.cp.id, []);
        var treeCfgQ = CpConfigSvc.getWorkflowData(scope.cp.id, 'specimenTree', {});
        $q.all([cpDictQ, treeCfgQ]).then(
          function(resps) {
            linker(scope, element, attrs, resps[0] || [], resps[1]);
          }
        );
      }
    }
  })

  .directive('osTreeNodeStatus', function($translate) {

    return {
      restrict: 'A',

      scope: {
        specimen: '=osTreeNodeStatus'
      },

      link: function(scope, element, attrs) {
        var specimen = scope.specimen, status = '';
        if (specimen.status == 'Collected') {
          if (specimen.activityStatus == 'Closed' && !specimen.distributionStatus) {
            status = 'closed';
          } else if (specimen.distributionStatus == 'Distributed') {
            status = specimen.availableQty > 0 ? 'part-distributed' : 'distributed';
          } else if (specimen.distributionStatus == 'Returned') {
            status = 'returned';
          } else if (specimen.reserved) {
            status = 'reserved';
          } else if (!specimen.storageLocation.name) {
            status = 'virtual';
          } else if (!specimen.reqId) {
            status = 'unplanned';
          } else {
            status = 'collected';
          }
        } else if (specimen.status == 'Missed Collection') {
          status = 'missed';
        } else if (specimen.status == 'Not Collected') {
          status = 'not-collected';
        } else if (!specimen.status || specimen.status == 'Pending') {
          status = 'pending';
        }

        element.addClass('os-status-' + status);

        var key = $translate.instant('specimens.tree_node_statuses.' + status);
        element.attr('title', key);
      }
    }
  });
