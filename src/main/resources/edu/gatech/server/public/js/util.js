function getPath(path) {
    return $.ajax({
        url: path,
        dataType: "json"
    });
}

function urlToParams(urlString) {
    var url = new URL(urlString);
    var postQ = url.search.split('?');
    if (postQ.size == 0) {
        return {};
    } else {
        var obj = {};
        postQ[1].split('&').map(seg => seg.split('=')).forEach((subsegs) => obj[subsegs[0]] = subsegs[1]);
        return obj;
    }
}

function wireForm(params) {
    console.log(params);
    var form = params['form'];
    var submit = params['submit'];
    var submitUrl = params['submitUrl'];
    var preSubmit = params['beforeSubmit'] || (function (d) { return d; });
    var redirectTo = params['redirectTo'] || document.location;
    function submitJson(data) {

        var jsObject = {};

        $.each(data, function(i, item) {
            jsObject[item.name] = item.value;
        });

        jsObject = preSubmit(jsObject);

        console.log("submitted data");
        console.log(jsObject);

        $.ajax({
            method : "POST",
            data: JSON.stringify(jsObject),
            contentType: "application/json; charset=utf-8",
            dataType: "json",
            url: submitUrl
        }).done(function(data){
            window.location.replace(redirectTo);
        }).fail(function(data) {
            console.log("failed response");
            console.log(data);
        });

    }
    form.off("submit");
    form.submit(function(e) { e.preventDefault(); submitJson(form.serializeArray()); });
    submit.off("click");
    submit.click(function(e) { e.preventDefault(); submitJson(form.serializeArray()) });
}

function addOptions(selectElement, optionValuesAndNames) {
  var optionsHtml = "";
  optionValuesAndNames.forEach(function(valueAndName) {
    var value = valueAndName[0];
    var name = valueAndName[1];
    optionsHtml = optionsHtml + "\n" + `<option value=${value}>${name}</option>`;
  })
  selectElement.html(optionsHtml);
  selectElement.change();
}


