function populateRoadInfo(elem, index, startSelect, endInput) {
  var endSelect = endInput;
  var data = {
    isForm : false,
    start : startSelect.val(),
    end : endSelect.val()
  }

  function update(prop, value) {
    data[prop] = value;
    getPath(`/bus/road?from=${data['start']}&to=${data['end']}`)
        .done(function(roadData){
            data['isForm'] = false;
            elem.html(`Speed Limit: ${roadData['speedLimit']}, Road Work: ${roadData['roadWork']}, traffic: ${roadData['traffic']}`);
        })
        .fail(function(error){
            if (data.isForm) {
              $("#endsAt-input-" + index).val(data['end']);
              $("#startsAt-input-" + index).val(data['start']);
            } else {
              data['isForm'] = true;
              var formContent = `
                <div class="road-input" data-id="${index}">
                  <label for="speedLimit-input-${index}">speed limit:</label>
                  <input id="speedLimit-input-${index}" data-id="${index}" type="number" name="speedLimit" />
                  <label for="roadWork-input-${index}">road work:</label>
                  <input id="roadWork-input-${index}" data-id="${index}" type="number" name="roadWork" />
                  <label for="traffic-input-${index}">traffic:</label>
                  <select id="traffic-input-${index}" data-id="${index}" name="traffic">
                    <option value="Heavy">heavy</option>
                    <option value="Normal">normal</option>
                  </select>
                  <input id="startsAt-input-${index}" type="hidden" name="beginsAt" value="${data['start']}" />
                  <input id="endsAt-input-${index}" type="hidden" name="endsAt" value="${data['end']}" />
                </div>`;
              elem.html(formContent);
            }
        })
  }

  function addStop(e, f) {
    if (f.newIndex == index + 1) {
        endSelect.off("change", updateEnd);
        endSelect = $(f.newId);
        endSelect.on("change", updateEnd);
        update('end', endSelect.val());
    }
  }

  var updateEnd = function(e) {
    update('end', e.target.value);
  };

  startSelect.change((e) => update('start', e.target.value));
  endSelect.on("change", updateEnd);
  elem.on("addStop", addStop);

  update('', 0);
}

function addNextStop(stopsData, routeKind) {
  var priorStopId = $("li.stop-choice").length;
  var newStopId = priorStopId + 1;
  var newStopHtml =
   `<li class="stop-choice" id="stop-choice-${newStopId}" data-index="${newStopId}">
    <div class="container">
      <div class="row">
        <div class="col-3">
          <label for="stop-id-${newStopId}-input">Stop ${newStopId}</label>
          <select class="stops-list" id="stop-id-${newStopId}-input" name="stop-id-${newStopId}" data-id="${newStopId}">
          </select>
        </div>
        <div class="col-8 road-info" id="road-info-${newStopId}">
        </div>
      </div>
    </div>
  </li>`
  $("#stops-list").append(newStopHtml);
  if (routeKind == "bus") {
    // roads are wrap-around, so the last stop should always have a road between it and the first...
    populateRoadInfo($("#road-info-" + newStopId), newStopId, $("#stop-id-" + newStopId + "-input"), $("#stop-id-1-input"));
    $(".road-info").trigger("addStop", { newIndex: newStopId, newId: "#stop-id-" + newStopId + "-input"});
  }
  addOptions($("#stop-id-" + newStopId + "-input"), stopsData);
}

function addStopIds(postData) {
  var stopsList = [];
  $("select.stops-list").each(function(i, selector) {
    var index = parseInt($(selector).attr("data-id"));
    stopsList[index - 1] = parseInt($(selector).val());
  });
  postData["stopNumbers"] = stopsList;
  return postData;
}

function addStopIdsAndRoads(postData) {
  var postDataWithStopIds = addStopIds(postData);
  postData['roads'] = {};
  $(".road-input").each(function(i, inputSelector){
    var input = $(inputSelector);
    var index = parseInt(input.attr("data-id"));
    postData['roads'][index] = {
      speedLimit: input.children("input[name='speedLimit']").val(),
      roadWork: input.children("input[name='roadWork']").val(),
      traffic: input.children("select[name='traffic']").val(),
      beginsAt: input.children("input[name='beginsAt']").val(),
      endsAt: input.children("input[name='endsAt']").val()
    }
  });
  return postData;
}
