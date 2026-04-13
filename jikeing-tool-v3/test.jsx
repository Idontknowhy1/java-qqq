// $.write(app)

// const test = () => {
//     app.project.items[2].openInViewer();
//     app.project.items[2].layers[2].selected=true;
//     return 1
// }

// var item = app.project.items[2];
// item.close();
// item.openInViewer();
// item.selected = true
// app.project.items[2].layers[2].selected=true;

// app.project.items[2].layers[2].enabled=false;

// app.project.selection = [app.project.items[2]]


// 操控
// app.project.activeItem.layer(1).effect("Puppet").property("arap").property("网格").property("网格 1").property("变形").numProperties

// app.project.activeItem.layer(1).effect("Puppet").property("arap").property("网格").property("网格 1").property("变形").property(1).position

// // 图层尺寸
// app.project.activeItem.layer(1).sourceRectAtTime(0, false).height
// effect("操控").arap.mesh("网格 1").deform("操控点 3").position


// 获取图层大小
function getLayerBounds(layer) {
    if (layer instanceof ShapeLayer) {
        return {
            width: app.project.activeItem.width,
            height: app.project.activeItem.height
        }
    } else {
        return layer.sourceRectAtTime(0, false)
    }
}

// 获取图层的图钉
function getLayerPins(layer) {
    var pins =[]
    // try {
        var pinPro = layer.effect("Puppet").property("arap").property("网格").property("网格 1").property("变形")
        var count = pinPro.numProperties
        for (var i = 1; i <= count; i++) {
            var pin = pinPro.property(i)
            pins.push({
                pin: pin,
                width: pin.position.value[0],
                height: pin.position.value[1],
            })
        }
    // } catch(err) {

    // }

    return pins
}

// var pins = getLayerPins(app.project.activeItem.layer(1))
// $.write(pins)

// 形状、文字的尺寸就是画布尺寸

// 获取PSD合成下的所有layer信息
function getLayersInPsdCompItem() {

    if (!app.project
        || !app.project.activeItem
        || app.project.activeItem.selectedLayers.length != 1
        || !app.project.activeItem.selectedLayers[0].source
        || !(app.project.activeItem.selectedLayers[0].source instanceof CompItem)) {
        alert("请先选择一个类型为\"合成\"的图层");
        return;
    }

    var psdItem = app.project.activeItem.selectedLayers[0].source
    if (psdItem.layers.length === 0) {
        alert("当前选择\"合成\"下没有图层内容");
        return;
    }

    // 收集psd合成下的所有layer
    var psdLayerResult = {
        itemId: psdItem.id, // psd合成的ID
        layers: []
    }
    for (var i = 1; i <= psdItem.numLayers; i++) {
        var layer = psdItem.layer(i)
        // $.writeln(layer.name + ' source = ' + layer.source.typeName);
        psdLayerResult.layers.push({
            id: layer.id,
            name: layer.name,
            type: (layer.source ? layer.source.typeName : ""),
            enabled: layer.enabled
        })
    }
    alert('names = ' + JSON.stringify(psdLayerResult))
}

// 根据合成ID，将合成打开
function getCompItem(itemId) {
    if (!app.project) {
        alert("当前项目未打开");
        return null;
    }

    var _targetItem = null;
    for (var i = 1; i <= app.project.numItems; i++) {
        var item = app.project.item(i)
        if (item.id === itemId) {
            _targetItem = item;
            break;
        }
    }

    if (_targetItem) {
        _targetItem.openInViewer()
    } else {
        alert("当前传入合成ID无效");
    }
    return _targetItem
}

// 根据合成Id，图层Id，获取图层对象
function getPsdLayer(itemId, layerId) {
    var _targetItem = getCompItem(itemId);

    var _targetLayer = null;
    for (var i = 1; i <= _targetItem.numLayers; i++) {
        var layer = _targetItem.layer(i)
        if (layer.id === layerId) {
            _targetLayer = layer;
            break;
        }
    }

    if (!_targetLayer) {
        alert("当前传入图层ID无效");
    }

    return { layer: _targetLayer, item: _targetItem }
}

// 根据选中合成下的某个Layer
function selectPsdLayer(itemId, layerId) {
    var result = getPsdLayer(itemId, layerId);
    if (result.layer) {
        result.item.openInViewer();
        result.layer.selected = true
    }
}

// 将合成下的图层显示或隐藏
function showTogglePsdLayer(itemId, layerId, show) {
    var result = getPsdLayer(itemId, layerId);
    if (result.layer) {
        result.layer.enabled = show
    }
}

// getLayersInPsdCompItem()
// openCompItem(741)
// selectPsdLayer(741, 754)
// showTogglePsdLayer(741, 754, true)


// 获取图层大小
const getLayerBounds = (layer) => {
  if (layer instanceof ShapeLayer) {
    return {
      width: app.project.activeItem.width,
      height: app.project.activeItem.height
    }
  } else {
    return layer.sourceRectAtTime(0, false)
  }
}

// 获取图层的图钉
const getLayerPins = (layer) => {
  let pins = []
  try {
    let pinPro = layer.effect("Puppet").property("arap").property("网格").property("网格 1").property("变形")
    let count = pinPro.numProperties
    for (var i = 1; i <= count; i++) {
      let pin = pinPro.property(i)
      pins.push({
        pin: pin,
        width: pin.position.value[0],
        height: pin.position.value[1],
      })
    }
  } catch (err) {

  }

  return pins
}



function getSelectedShapeVertices() {
    var comp = app.project.activeItem;
    if (!(comp && comp instanceof CompItem)) return [];

    var selProps = comp.selectedProperties;
    var result = [];

    for (var i = 0; i < selProps.length; i++) {
        var prop = selProps[i];
        if (prop.matchName === "ADBE Vector Shape") {
            var verticesInfo = prop.selectedVertices; // ✅ 这个在 Shape Path 上可用
            var path = prop.value;
            for (var v = 0; v < verticesInfo.length; v++) {
                if (verticesInfo[v] === true) {
                    result.push(path.vertices[v]); // 顶点坐标
                }
            }
        }
    }
    return result;
}

var result = getSelectedShapeVertices()



var selectedProperties = app.project.activeItem.selectedProperties

function getSelectedPinIds() {
    var num = selectedProperties.length
    var pins = []
    for (var i = 0; i < num; i++) {
        var p = selectedProperties[i]
        // if (p instanceof)
        if (p.matchName === 'ADBE FreePin3 PosPin Atom') {
            pins.push(p.id)
        }
    }
    return pins
}

var result = getSelectedPins()
var r = 0