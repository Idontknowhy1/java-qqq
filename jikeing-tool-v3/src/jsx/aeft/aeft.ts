
import {
  helloVoid,
  helloError,
  helloStr,
  helloNum,
  helloArrayStr,
  helloObj,
} from "../utils/samples";
export { helloError, helloStr, helloNum, helloArrayStr, helloObj, helloVoid };
import { dispatchTS, forEach } from '../utils/utils';

var mergeAudioCompItem: CompItem | null = null;
var selectedKeys = null;
//main pin name to swaydata
var swayMap = {};

interface SwayData {
  layer: Layer,
  point: any
}

interface Config {
  resourceRootPath: string,
}

export interface FileRef {
  name: string,
  path: string,
  parentPath: string,
  children: Array<FileRef>
  isFileOrFolder: boolean,
}

let resourceRootPath = '';

export function getResourceRootPath(): string {
  if (resourceRootPath.length == 0) {
    const configFile = new File(`${Folder.myDocuments.fsName}/jike/config.json`);
    let json = '{}';
    if (configFile.exists) {
      configFile.open('r');
      json = configFile.read();
      configFile.close();
    }
    const config = JSON.parse(json) as Config;
    resourceRootPath = config.resourceRootPath;
    if (resourceRootPath.length == 0) {
      resourceRootPath = `${Folder.myDocuments.fsName}/jike`
    }
  }
  return resourceRootPath;
}

function saveConfig() {
  const newConfig = {
    resourceRootPath: resourceRootPath
  };
  const json = JSON.stringify(newConfig);
  const jikeFolder = new Folder(`${Folder.myDocuments.fsName}/jike`);
  if (jikeFolder.exists == false) {
    jikeFolder.create()
  }
  const configFile = new File(`${Folder.myDocuments.fsName}/jike/config.json`);
  configFile.open('w');
  configFile.write(json);
  configFile.close();
}

export function selectFolder(): string {
  const selectedFolder = Folder.selectDialog(`请选择文件夹`);
  resourceRootPath = selectedFolder.fsName;
  saveConfig();
  return selectedFolder.fsName
}

export function getFileBySuffix(dirPath: string, suffix: string): Array<FileRef> {
  $.writeln(`getFileBySuffix:${dirPath},${suffix}`);
  const folder = new Folder(dirPath);
  const files = folder.getFiles(`*.${suffix}`);
  const result: Array<FileRef> = [];
  for (let i = 0; i < files.length; i++) {
    const file = files[i];
    const fileName = decodeURIComponent(file.name);

    result.push({
      name: fileName,
      path: file.fsName,
      parentPath: dirPath,
      children: [],
      isFileOrFolder: true
    });
  }
  return result
}

export function saveCurrentProject2Folder(folder: string) {
  $.writeln(`保存当前项目到目录:${folder}`);
  const selectedItem = app.project.activeItem;
  if (selectedItem == null) {
    alert(`当前没有打开工程`);
    return;
  }
  const currentProjectFile = app.project.file;
  if (currentProjectFile == null)
    return;
  $.writeln(`当前工程路径:${currentProjectFile.fsName}`);
  const name = decodeURIComponent(currentProjectFile.name);
  const targetFile = new File(`${folder}/${name}`)
  currentProjectFile.copy(targetFile.fsName);
  alert(`保存成功`);
}

export function fileExists(path: string): boolean {
  const file = new File(path);
  return file.exists;
}

export function addEffect(path: string) {
  const selectedItem = app.project.activeItem;
  if (selectedItem == null) {
    alert(`请先选中图层`);
    return;
  }
  var selectedLayer = (selectedItem as CompItem).selectedLayers;
  if (selectedLayer.length == 0) {
    alert(`请先选中图层`);
    return;
  }
  const ffxFile = new File(path);
  var layer = selectedLayer[0];
  app.beginUndoGroup(`添加效果`);
  layer.applyPreset(ffxFile);
  app.endUndoGroup();
}

export function getVideoFileList(path: string): Array<FileRef> {
  $.writeln(`getVideoList:${path}`);
  const folder = new Folder(path);
  const result: Array<FileRef> = [];
  const mp4List = folder.getFiles("*.mp4");
  const movList = folder.getFiles("*.mov");
  fileIntoFileRef(path, mp4List, result);
  fileIntoFileRef(path, movList, result);
  return result;
}

function fileIntoFileRef(path: string, fileList: (File | Folder)[], result: Array<FileRef>) {
  for (let i = 0; i < fileList.length; i++) {
    const video = fileList[i];
    const fileName = decodeURIComponent(video.name);
    result.push({
      name: fileName,
      path: video.fsName,
      parentPath: path,
      children: [],
      isFileOrFolder: true
    });
  }
}

export function getEffectFile(path: string): Array<FileRef> {
  $.writeln(`getEffectFile:${path}`);
  const folder = new Folder(path);
  const result: Array<FileRef> = [];
  const fileList = folder.getFiles("*.ffx");
  for (let i = 0; i < fileList.length; i++) {
    const ffx = fileList[i];
    const fileName = decodeURIComponent(ffx.name);
    result.push({
      name: fileName,
      path: ffx.fsName,
      parentPath: path,
      children: [],
      isFileOrFolder: true
    });
  }
  return result;
}

export function openTxtFile(path: string) {
  $.writeln(`openTxt:${path}`);
  const txtFile = new File(path);
  txtFile.execute();
}

export function getScriptList(path: string) {
  $.writeln(`getScriptList:${path}`);
  const folder = new Folder(path);
  const result: Array<FileRef> = [];
  const fileList = folder.getFiles("*.txt");
  for (let i = 0; i < fileList.length; i++) {
    const txt = fileList[i];
    const fileName = decodeURIComponent(txt.name);
    result.push({
      name: fileName,
      path: txt.fsName,
      parentPath: path,
      children: [],
      isFileOrFolder: true
    });
  }
  return result;
}

export function addResource(path: string) {
  const file = new File(path);
  if (!file.exists) {
    alert(`文件异常，添加失败`);
    return;
  }
  app.beginUndoGroup(`添加素材音频`);
  const importOptions = new ImportOptions(file);
  const footageItem = app.project.importFile(importOptions) as FootageItem;
  const comp = app.project.activeItem;
  if (comp == null) {
    app.endUndoGroup();
    alert(`已添加音频到素材列表，但是没有打开合成无法添加到时间线上`);
    return;
  }
  if (!(comp instanceof CompItem)) {
    app.endUndoGroup();
    return;
  }
  const assetLayer = (comp as CompItem).layers.add(footageItem);
  assetLayer.startTime = comp.time;
  app.endUndoGroup();
}

export function renameByPath(path: string, newName: string) {
  $.writeln(`根据完整路径改名:${path},${newName}`);
  const file = new File(path);
  file.rename(newName);
}

export function openProject(path: string) {
  $.writeln(`打开工程:${path}`);
  app.open(new File(path));
}

export function getAudioList(dirPath: string): Array<FileRef> {
  $.writeln(`getAudioList:${dirPath}`);
  const folder = new Folder(dirPath);
  const result: Array<FileRef> = [];
  const supportFormat = ["*.mp3", "*.aac", "*.wav", "*.m4a"];
  for (let i = 0; i < supportFormat.length; i++) {
    const format = supportFormat[i];
    const audioFiles = folder.getFiles(format);
    for (let j = 0; j < audioFiles.length; j++) {
      const audio = audioFiles[j];
      const fileName = decodeURIComponent(audio.name);
      result.push({
        name: fileName,
        path: audio.fsName,
        parentPath: dirPath,
        children: [],
        isFileOrFolder: true
      });
    }
  }
  return result;
}

export function renameProject(path: string, newName: string) {
  const folder = new Folder(path);
  if (folder.exists) {
    $.writeln(`重命名:${folder.name}为${newName}`);
    folder.rename(newName);
  }
}

export function deleteFile(path: string) {
  $.writeln(`删除文件：${path}`);
  const folder = new Folder(path);
  folder.remove();

  const name = path.substring(0, path.lastIndexOf('.'))
  const cover1 = new File(name + ".jpg");
  if (cover1.exists)
    cover1.remove();
  const cover2 = new File(name + ".gif");
  if (cover2.exists) {
    cover2.remove();
  }
}

export function createFolder(type: string, name: string, parent: string | null): boolean {
  $.writeln(`创建文件夹:${type},${parent},name:${name}`);
  let childPath = "";
  if (parent != null) {
    childPath = `${parent}/${name}`;
  }
  else
    childPath = name;

  const folder = new Folder(`${getResourceRootPath()}/${type}/${childPath}`);
  $.writeln(folder.fsName);
  if (folder.exists)
    return true;
  return folder.create();
}

export function getFolderContent(type: string, fileSuffix: string, fileSuffix2: string | null = null): Array<FileRef> {
  const folder = new Folder(`${getResourceRootPath()}/${type}`);
  if (!folder.exists) {
    folder.create();
  }
  const fileList = folder.getFiles();
  const result: FileRef[] = [];
  $.writeln(`获取${type}资源目录:`);
  for (let i = 0; i < fileList.length; i++) {
    const file = fileList[i];
    const fileName = decodeURIComponent(file.name);
    $.writeln(fileName);
    if (file instanceof Folder) {
      const children: Array<FileRef> = [];
      const fileChildren = file.getFiles();
      for (let j = 0; j < fileChildren.length; j++) {
        const child = fileChildren[j];
        const childFileName = decodeURIComponent(child.name);
        const lastDotIndex = childFileName.lastIndexOf(".");
        const prefix = childFileName.substring(lastDotIndex + 1);
        if ((child instanceof Folder) || (prefix == fileSuffix) || (prefix == fileSuffix2)) {
          children.push({
            name: decodeURIComponent(child.name),
            path: child.fsName,
            parentPath: file.fsName,
            children: [],
            isFileOrFolder: !(child instanceof Folder)
          });
        }
      }
      result.push({
        name: fileName,
        path: fileList[i].fsName,
        parentPath: folder.fsName,
        children: children,
        isFileOrFolder: false
      })
    }
  }
  return result
}

export function cloneKeyframe(reverse: boolean) {
  const projectItem = app.project.activeItem;
  if (projectItem == null)
    return;
  const activeItem = projectItem as CompItem
  const curSelectedKeys = getSelectedKey();
  if (curSelectedKeys == null) {
    alert(`当前没有选中关键帧`);
    return;
  }

  app.beginUndoGroup(`克隆关键帧`);
  const curTime = activeItem.time;
  for (var key in curSelectedKeys) {
    let keyFrameData = curSelectedKeys[key];
    for (let i = 0; i < keyFrameData.length; i++) {
      let keyframeUnit = keyFrameData[i];
      let prop = keyframeUnit.prop;
      if (keyframeUnit.keyframeIndex.length > 0) {
        const keyTimeValue = [];
        for (let j = 0; j < keyframeUnit.values.length; j++) {
          const keyTime = prop.keyTime(keyframeUnit.keyframeIndex[j]);
          const value = keyframeUnit.values[j];
          keyTimeValue.push({
            keyTime: keyTime,
            value: value
          })
        }

        if (reverse) {
          let lastTime = keyTimeValue[keyTimeValue.length - 1].keyTime;
          for (let j = keyTimeValue.length - 1; j >= 0; j--) {
            let value = keyTimeValue[j].value;
            let diffTime = lastTime - keyTimeValue[j].keyTime;
            let newKeyTime = curTime + diffTime;
            $.writeln(`设置关键帧 ${prop.name}:${newKeyTime}.[${value}]`);
            keyframeUnit.prop.setValueAtTime(newKeyTime, value);
          }
        }
        else {
          let firstKeyFrameTime = keyTimeValue[0].keyTime;
          for (let j = 0; j < keyTimeValue.length; j++) {
            let value = keyTimeValue[j].value;
            let diffTime = keyTimeValue[j].keyTime - firstKeyFrameTime;
            let newKeyTime = curTime + diffTime;
            $.writeln(`设置关键帧 ${prop.name}:${newKeyTime}.[${value}]`);
            keyframeUnit.prop.setValueAtTime(newKeyTime, value);
          }
        }
      }
    }
  }
  app.endUndoGroup();
}

export function copyToClipboard(text: string) {
  var clipboard = new XML('clipboard');
  clipboard.encoding = 'TEXT';
  clipboard.content = text;
  return true;
}

export function writeln(message: any) {
  $.writeln(message);
}

export function clearSwayElement() {
  const activeItem = app.project.activeItem;
  if (activeItem == null)
    return;
  const selectedLayer = (activeItem as CompItem).selectedLayers;
  const validKeys: string[] = [];
  for (var i = 0; i < selectedLayer.length; i++) {
    const layer = selectedLayer[i];
    if (swayMap[layer.name] != null) {
      validKeys.push(layer.name);
    }
  }
  if (validKeys.length <= 0) {
    alert(`请先选中要移除的主图钉`);
    return;
  }

  app.beginUndoGroup(`移除飘动`);
  for (var i = 0; i < validKeys.length; i++) {
    const key = validKeys[i];
    const swayDataList = swayMap[key] as SwayData[];
    for (var j = 0; j < swayDataList.length; j++) {
      const swayData = swayDataList[j];
      swayData.layer.remove();
      swayData.point.position.expression = "";
    }
    swayDataList.length = 0;
    delete swayMap[key];
  }
  app.endUndoGroup();

  let keyCount = 0;
  for (key in swayMap) {
    keyCount++;
  }
  $.writeln(`清理飘动图钉后,剩余飘动数量：${keyCount}`);
}

export function onDeleteKeyframe() {
  selectedKeys = null;
}

/**
 * 飘动
 */
export function sway() {
  var activeItem = app.project.activeItem;
  if (activeItem == null || !(activeItem instanceof CompItem))
    return;
  var selectedLayer = activeItem.selectedLayers;
  if (selectedLayer.length == 0) {
    alert(`请先选中一个图层`);
    return;
  }

  var layer = selectedLayer[0];
  var puppetPin = layer.effect("Puppet");
  if (!puppetPin)
    return;
  var mesh = puppetPin.property("arap").property(`网格`).property(`网格 1`)
  if (!mesh)
    return;
  var deform = mesh.property(`变形`)
  if (!deform)
    return;

  var selectedPoint = [];
  for (var i = 1; i <= deform.numProperties; i++) {
    var prop = deform.property(i);
    if (prop.selected)
      selectedPoint.unshift(prop);
  }

  var selectedPointsName = "";
  for (var i = 0; i < selectedPoint.length; i++)
    selectedPointsName += selectedPoint[i].name += " ";
  $.writeln(`选中了:${selectedPointsName}`);
  if (selectedPoint.length <= 1) {
    alert(`至少选中两个点`);
    return;
  }

  let swayCount = 0;
  for (var key in swayMap) {
    if (swayMap.hasOwnProperty(key))
      swayCount++;
  }

  app.beginUndoGroup(`飘动`);

  var pinSolid = activeItem.layers.addSolid([0, 0, 0], `图钉`, 20, 20, 1.0);
  pinSolid.moveBefore(layer)
  var tmpName = `主图钉`;
  var nameIndex = 1;
  while (swayMap[`${tmpName}-${nameIndex}`] != null) {
    nameIndex++;
  }
  pinSolid.name = `${tmpName}-${nameIndex}`;
  swayMap[pinSolid.name] = [];
  swayMap[pinSolid.name].push({
    layer: pinSolid,
    point: selectedPoint[0]
  } as SwayData);
  pinSolid.opacity.setValue(0);
  const scriptParentFolder = File($.fileName).parent;
  const swayFFXFile = File(scriptParentFolder.path + "/public/sway.ffx");
  pinSolid.applyPreset(swayFFXFile);
  pinSolid.property(`ADBE Effect Parade`)[`飘动图钉`][`偏移`].setValue((swayCount + 1) * 8);

  pinSolid.parent = layer;
  var firstPoint = selectedPoint[0];
  var secondPoint = selectedPoint[1];
  var Ax = firstPoint.position.value[0],
    Ay = firstPoint.position.value[1];
  var Bx = secondPoint.position.value[0],
    By = secondPoint.position.value[1];
  var dx = Bx - Ax;
  var dy = By - Ay;
  var angleAB = Math.atan2(dy, dx) * 180 / Math.PI;
  pinSolid.rotation.setValue(angleAB - 90);
  pinSolid.position.setValue(selectedPoint[0].position.value);

  var firstSolid = null;
  var lastSolid = null;
  var rotationCount = pinSolid.rotation.value;
  for (var i = 0; i < selectedPoint.length; i++) {
    var point = selectedPoint[i];
    var pinSolidPart = activeItem.layers.add(pinSolid.source);
    pinSolidPart.moveBefore(layer)
    swayMap[pinSolid.name].push({
      layer: pinSolidPart,
      point: point
    } as SwayData);
    pinSolidPart.opacity.setValue(0);
    pinSolidPart.name = `子图钉${(i + 1)}-${nameIndex}`;
    pinSolidPart.parent = layer;
    pinSolidPart.position.setValue(point.position.value);

    if (i == 0) {
      pinSolidPart.rotation.setValue(pinSolid.rotation.value);
      pinSolidPart.parent = pinSolid;
      $.writeln(`${i}:${pinSolidPart.position.value}`);
      firstSolid = pinSolidPart;
      var pinSolid2Effects = pinSolidPart.property("ADBE Effect Parade");
      var windStrength = pinSolid2Effects.addProperty("ADBE Slider Control");
      var windStrengthIndex = windStrength.propertyIndex;
      pinSolid2Effects.property(windStrengthIndex).name = "Strength of the wind[delete prohibited]";

      // seedRandom(12, timeless = false);
      // freq = Math.max(thisComp.layer('[SwayControl1]_裙子 合成 1').effect('AutoSway Pin')('Wind interval'))*0.01;
      // amp = Math.max(thisComp.layer('[SwayControl1]_裙子 合成 1').effect('AutoSway Pin')('Wind strength'))*0.2;
      // loopSec= Math.max(thisComp.layer('[SwayControl1]_裙子 合成 1').effect('AutoSway Pin')('Wind loop period (seconds)'));
      // if(loopSec<=0){loopSec=0.1}t = time % loopSec;
      // wiggle1 = wiggle(freq, amp, 1, 0.5, t);
      // wiggle2 = wiggle(freq, amp, 1, 0.5, t - loopSec);
      // wiggle3=wiggle(freq, ease(t, 0, loopSec, wiggle1, wiggle2), 1, 0.5, t);
      // wiggle4=wiggle(freq, ease(t, 0, loopSec, wiggle1, wiggle2), 1, 0.5, t- loopSec);
      // ease(t, 0, loopSec, wiggle3, wiggle4)
      pinSolid2Effects.property(windStrengthIndex)(`滑块`).expression = `
      seedRandom(12, timeless = false);
      freq = Math.max(thisComp.layer('${pinSolid.name}').effect('飘动图钉')('风动间隔'))*0.01;
      amp = Math.max(thisComp.layer('${pinSolid.name}').effect('飘动图钉')('风动强度'))*0.2;
      loopSec= Math.max(thisComp.layer('${pinSolid.name}').effect('飘动图钉')('风动循环周期(秒)'));
      if(loopSec<=0){loopSec=0.1}t = time % loopSec;
      wiggle1 = wiggle(freq, amp, 1, 0.5, t);
      wiggle2 = wiggle(freq, amp, 1, 0.5, t - loopSec);
      wiggle3=wiggle(freq, ease(t, 0, loopSec, wiggle1, wiggle2), 1, 0.5, t);
      wiggle4=wiggle(freq, ease(t, 0, loopSec, wiggle1, wiggle2), 1, 0.5, t- loopSec);
      ease(t, 0, loopSec, wiggle3, wiggle4)
      `;

      pinSolidPart.rotation.expression = `
      var r=thisComp.layer('${pinSolidPart.name}').effect('Strength of the wind[delete prohibited]')('ADBE Slider Control-0001');
      var rota=thisComp.layer('${pinSolid.name}').effect('飘动图钉')('旋转');
      var ca=thisComp.layer('${pinSolid.name}').effect('飘动图钉')('曲线调整')*0.05;
      [transform.rotation+ca*-1+rota+r*0.1];
      `;
    } else {
      pinSolidPart.parent = lastSolid;
      $.writeln(`${i}:${pinSolidPart.position.value}`);
      pinSolidPart.position.expression = genSwaySolidPartPositionExpression(pinSolid.name, firstSolid.name, i);
      pinSolidPart.rotation.expression = genSwaySolidPartRotationExpression(pinSolid.name, firstSolid.name);

      if (i < selectedPoint.length - 1) {
        var nextPoint = selectedPoint[i + 1];
        var Ax = point.position.value[0],
          Ay = point.position.value[1];
        var Bx = nextPoint.position.value[0],
          By = nextPoint.position.value[1];
        var dx = Bx - Ax;
        var dy = By - Ay;
        var oldRotation = pinSolidPart.rotation.value;
        var angleAB = Math.atan2(dy, dx) * 180 / Math.PI - 90 - rotationCount;
        pinSolidPart.rotation.setValue(angleAB);
        rotationCount += angleAB;
      } else {
        pinSolidPart.rotation.setValue(0);
        $.writeln(`pinSolidPart.name rotation:${rotationCount}`);
      }
    }

    point.position.expression = "Pin = thisComp.layer('" + pinSolidPart.name + "').toWorld(thisComp.layer('" + pinSolidPart.name + "').anchorPoint);\n" +
      "fromWorld(Pin)";
    lastSolid = pinSolidPart;
  }

  app.endUndoGroup();
}

function genSwaySolidPartPositionExpression(mainPinName: string, parentName: string, no: number) {
  var expressionStr =
    `var no=${no};
    loopSec= 1;
    var d=thisComp.layer('${mainPinName}').effect('飘动图钉')('飘动衰减')*0.01;
    if(d<0){d=0};
    var r=thisComp.layer('${parentName}').effect('Strength of the wind[delete prohibited]')('ADBE Slider Control-0001');
    kaze=0.01*(r*0.01);
    var l = thisComp.layer('${mainPinName}').effect('飘动图钉')('飘动距离')*0.5;
    var g = thisComp.layer('${mainPinName}').effect('飘动图钉')('飘动速度')*0.01;
    var e=thisComp.layer('${mainPinName}').effect('飘动图钉')('飘动圆度')*0.001+r*0.005;
    var z=thisComp.layer('${mainPinName}').effect('飘动图钉')('滞后因子')*0.001;
    var y=thisComp.layer('${mainPinName}').effect('飘动图钉')('缩放Y');
    var x=thisComp.layer('${mainPinName}').effect('飘动图钉')('缩放X')*0.01;
    var t = thisComp.layer('${mainPinName}').effect('飘动图钉')('偏移')*0.1;
    try{nKey =thisComp.layer('${mainPinName}').effect('飘动图钉')('飘动衰减').nearestKey(0)
    if(time >= nKey.time ) {gtime=time-nKey.time}else{gtime=time-nKey.time;if(gtime<0){d=0}}}catch(e){gtime=time};
    t=t+(no*z*10);
    no=no*0.01;
    z=z*no*3;
    x=x+no*z*50;
    y=y+no*z*50;
    e=e+z;
    var sin =loopSec*e*Math.sin(time*Math.PI*g+t);
    [transform.position[0] + (x+l)*Math.sin(kaze+sin)/Math.exp(d*gtime), transform.position[1] - (y+l)*(1-Math.cos(kaze+sin))/Math.exp(d*gtime)]
    `;
  return expressionStr;
}

function genSwaySolidPartRotationExpression(mainPinName: string, parentName: string) {
  var expressionStr =
    `var r=thisComp.layer('${parentName}').effect('Strength of the wind[delete prohibited]')('ADBE Slider Control-0001');
  var ca=thisComp.layer('${mainPinName}').effect('飘动图钉')('曲线调整')*0.05;
  var sta=false;
  if(sta==true){if(ca>=1*0.05||ca<=-1*0.05){[0+ca+(r*0.2)]}else{[0+ca]}};
  if(sta==false){if(ca>=1*0.05||ca<=-1*0.05){[transform.rotation+ca+(r*0.2)]}else{[transform.rotation+ca+r*0.2]}};`;
  return expressionStr;
}

export function addBoneEffect() {
  var selectedLayer = app.project.activeItem.selectedLayers;
  if (selectedLayer.length == 0) {
    alert(`请先至少选中1个图层`);
    return;
  }
  var layer = selectedLayer[0];
  var effectGroup = layer.property("ADBE Effect Parade");
  app.beginUndoGroup(`添加骨骼效果`);
  try {
    effectGroup.addProperty("Bones");
  } catch (e) {
    alert(`请先安装骨骼插件`);
  }
  app.endUndoGroup();
}

export function alignLayerByLayer(amount: number, step: number) {
  var selectedLayer = app.project.activeItem.selectedLayers;
  if (selectedLayer.length < 2) {
    alert(`请先至少选中2个图层`);
    return;
  }

  app.beginUndoGroup(`图层与时间线对齐`);
  var frameDuration = app.project.activeItem.frameDuration;
  var firstInPoint = selectedLayer[0].inPoint;
  var dividerTime = selectedLayer[1].inPoint - firstInPoint;
  var isSameDivider = true;
  var epsilon = 0.03333333333333;

  var lastInPoint = selectedLayer[0].inPoint;
  const totalDuration = frameDuration * amount;
  for (var i = 1; i < selectedLayer.length / step; i++) {
    for (var j = 0; j < step; j++) {
      var layer = selectedLayer[i * step + j];
      var outPoint = layer.outPoint;
      layer.inPoint = lastInPoint + totalDuration;
      layer.outPoint = outPoint + totalDuration;
    }
    lastInPoint = lastInPoint + totalDuration;
  }

  // for (var i = 2; i < selectedLayer.length; i++) {
  //   var layer = selectedLayer[i];
  //   var dividerTime2 = layer.inPoint - lastInPoint;
  //   if (Math.abs(dividerTime - dividerTime2) > epsilon) {
  //     isSameDivider = false;
  //     break;
  //   }
  //   lastInPoint = layer.inPoint;
  // }
  // if (isSameDivider) {
  //   dividerTime += frameDuration;
  // }

  // lastInPoint = selectedLayer[0].inPoint;
  // for (var i = 1; i < selectedLayer.length; i++) {
  //   var layer = selectedLayer[i];
  //   var outPoint = layer.outPoint;
  //   layer.inPoint = lastInPoint + dividerTime;
  //   layer.outPoint = outPoint;
  //   lastInPoint = layer.inPoint;
  // }
  app.endUndoGroup();
}

export function alignCurrentTime() {
  app.beginUndoGroup(`图层与时间线对齐`);
  var selectedLayer = app.project.activeItem.selectedLayers;
  var curTime = app.project.activeItem.time;
  for (var i = 0; i < selectedLayer.length; i++) {
    var layer = selectedLayer[i];
    layer.inPoint = curTime;
  }
  app.endUndoGroup();
}

export function placeBounceExpress() {
  const amplitudeName = `振幅`;
  const gravityName = `重力`;
  const maxBounce = `最大弹跳`;
  const slidingName = `滑块`;
  app.beginUndoGroup(`添加弹性表达式`);
  var expression = "try {\n" +
    "  elastic = effect(\"" + amplitudeName + "\")(\"" + slidingName + "\").value / 200;\n" +
    "  gravity = effect(\"" + gravityName + "\")(\"" + slidingName + "\").value * 150;\n" +
    "  bounceMax = effect(\"" + maxBounce + "\")(\"" + slidingName + "\").value;\n" +
    "  on_off = false;\n" +
    "\n" +
    "  n = 0;\n" +
    "  if (numKeys > 0) {\n" +
    "    n = nearestKey(time).index;\n" +
    "    if (key(n).time > time) {\n" +
    "      n--;\n" +
    "    }\n" +
    "  }\n" +
    "  if (n > 0) {\n" +
    "    t = time - key(n).time;\n" +
    "    v = -velocityAtTime(key(n).time - 0.001) * elastic;\n" +
    "    vl = length(v);\n" +
    "    if (value instanceof Array) {\n" +
    "      vu = (vl > 0) ? normalize(v) : [0, 0, 0];\n" +
    "    } else {\n" +
    "      vu = (v < 0) ? -1 : 1;\n" +
    "    }\n" +
    "    tCur = 0;\n" +
    "    segDur = 2 * vl / gravity;\n" +
    "    tNext = segDur;\n" +
    "    numberOfBounces = 1;\n" +
    "    while (tNext < t && numberOfBounces <= bounceMax) {\n" +
    "      vl *= elastic;\n" +
    "      segDur *= elastic;\n" +
    "      tCur = tNext;\n" +
    "      tNext += segDur;\n" +
    "      numberOfBounces++;\n" +
    "    }\n" +
    "    if (numberOfBounces <= bounceMax) {\n" +
    "      delta = t - tCur;\n" +
    "      inOutExp = vu * delta * (vl - gravity * delta / 2);\n" +
    "      if (on_off == 1) {\n" +
    "        value = value - inOutExp;\n" +
    "      } else {\n" +
    "        value = value + inOutExp;\n" +
    "      }\n" +
    "    } else {\n" +
    "      value = value;\n" +
    "    }\n" +
    "  } else {\n" +
    "    value = value;\n" +
    "  }\n" +
    "} catch (e$$4) {\n" +
    "  value = value;\n" +
    "}";
  var selectedLayer = app.project.activeItem.selectedLayers;
  for (var i = 0; i < selectedLayer.length; i++) {
    var layer = selectedLayer[i];
    var effectGroup = layer.property("ADBE Effect Parade");
    var slider1 = effectGroup.addProperty("ADBE Slider Control");
    var slider1Index = slider1.propertyIndex;
    var slider2 = effectGroup.addProperty("ADBE Slider Control");
    var slider2Index = slider2.propertyIndex;
    var slider3 = effectGroup.addProperty("ADBE Slider Control");
    var slider3Index = slider3.propertyIndex;
    effectGroup.property(slider1Index).name = `振幅`;
    effectGroup.property(slider1Index)(`滑块`).setValue(50);
    effectGroup.property(slider2Index).name = `重力`;
    effectGroup.property(slider2Index)(`滑块`).setValue(15);
    effectGroup.property(slider3Index).name = `最大弹跳`;
    effectGroup.property(slider3Index)(`滑块`).setValue(5);

    var selectedProperties = layer.selectedProperties;
    for (var j = 0; j < selectedProperties.length; j++) {
      var property = selectedProperties[j];
      if (property.canSetExpression) {
        property.expression = expression;
      }
    }
  }
  app.endUndoGroup();
}

export function placeElasticExpress() {
  app.beginUndoGroup(`添加弹性表达式`);
  const amplitudeName = `振幅`;
  const frequencyName = `频率`;
  const decayName = `衰减`;
  const slidingName = `滑块`;
  var expression =
    "try {\n" +
    "  amp = effect(\`" + amplitudeName + "\`)(\`" + slidingName + "\`).value/ 10 / 100;\n" +
    "  freq = effect(\`" + frequencyName + "\`)(\`" + slidingName + "\`).value/ 100;\n" +
    "  decay = effect(\`" + decayName + "\`)(\`" + slidingName + "\`).value / 100;\n" +
    "  function elastic(amp, freq, decay) {\n" +
    "    function calc(n) {\n" +
    "      var t = time - key(n).time;\n" +
    "      var v = velocityAtTime(key(n).time - thisComp.frameDuration/10);\n" +
    "      return v * ((amp / freq)) * Math.sin(freq * t * 2*Math.PI) / Math.exp(t * (decay * 2) * Math.E);\n" +
    "    };\n" +
    "    if (numKeys == 0){ return 0; };\n" +
    "    var n = nearestKey(time).index;\n" +
    "    if (key(n).time > time) n--;\n" +
    "    return (n > 1 && time <= key(n).time + (1 / decay)) ? calc(n) + calc(n - 1) : 0;\n" +
    "  };\n" +
    "  value + elastic(amp, freq, decay);\n" +
    "} catch (e) {\n" +
    "  value = value;\n" +
    "}";
  var selectedLayer = app.project.activeItem.selectedLayers;
  for (var i = 0; i < selectedLayer.length; i++) {
    var layer = selectedLayer[i];
    var effectGroup = layer.property("ADBE Effect Parade");
    var slider1 = effectGroup.addProperty("ADBE Slider Control");
    var slider1Index = slider1.propertyIndex;
    var slider2 = effectGroup.addProperty("ADBE Slider Control");
    var slider2Index = slider2.propertyIndex;
    var slider3 = effectGroup.addProperty("ADBE Slider Control");
    var slider3Index = slider3.propertyIndex;
    effectGroup.property(slider1Index).name = `振幅`;
    effectGroup.property(slider1Index)(`滑块`).setValue(100);
    effectGroup.property(slider2Index).name = `频率`;
    effectGroup.property(slider2Index)(`滑块`).setValue(100);
    effectGroup.property(slider3Index).name = `衰减`;
    effectGroup.property(slider3Index)(`滑块`).setValue(100);


    var selectedProperties = layer.selectedProperties;
    for (var j = 0; j < selectedProperties.length; j++) {
      var property = selectedProperties[j];
      if (property.canSetExpression) {
        property.expression = expression;
      }
    }
  }
  app.endUndoGroup();
}

export function changeAnchorPosition(type: number) {
  const activeItem = app.project.activeItem;
  if (activeItem == null) {
    return;
  }
  if (!(activeItem instanceof CompItem)) {
    return;
  }
  var selectedLayers = (activeItem as CompItem).selectedLayers;
  if (selectedLayers.length <= 0)
    return;

  app.beginUndoGroup(`改变锚点位置`);
  const curTime = activeItem.time;
  for (var i = 0; i < selectedLayers.length; i++) {
    const layer = selectedLayers[i];
    const rect = layer.sourceRectAtTime(0, false);
    const targetAnchorPos = [rect.left, rect.top];
    const width = rect.width;
    const height = rect.height;
    switch (type) {
      case 2: {
        targetAnchorPos[0] += width / 2;
        break;
      }
      case 3: {
        targetAnchorPos[0] += width;
        break;
      }
      case 4: {
        targetAnchorPos[1] += height / 2;
        break;
      }
      case 5: {
        targetAnchorPos[0] += width / 2;
        targetAnchorPos[1] += height / 2;
        break;
      }
      case 6: {
        targetAnchorPos[0] += width;
        targetAnchorPos[1] += height / 2;
        break;
      }
      case 7: {
        targetAnchorPos[1] += height;
        break;
      }
      case 8: {
        targetAnchorPos[0] += width / 2;
        targetAnchorPos[1] += height;
        break;
      }
      case 9: {
        targetAnchorPos[0] += width;
        targetAnchorPos[1] += height;
        break;
      }
    }
    // if (layer instanceof TextLayer) {
    //   targetAnchorPos[1] = targetAnchorPos[1] - height;
    // }

    if (layer.position.numKeys == 0) {
      const anchorPos = layer.sourcePointToComp(targetAnchorPos);
      layer.anchorPoint.setValue(targetAnchorPos);
      layer.position.setValue(anchorPos);
    }
    else {
      const scaleKeyframe = [];
      for (let j = 1; j <= layer.scale.numKeys; j++) {
        const keyTime = layer.scale.keyTime(j);
        const keyValue = layer.scale.keyValue(j);
        scaleKeyframe.push({
          keyTime: keyTime,
          keyValue: keyValue
        });
      }

      const scaleKeyCount = layer.scale.numKeys;
      const curScale = layer.scale.valueAtTime(curTime, true);
      for (let j = 0; j < scaleKeyCount; j++) {
        layer.scale.removeKey(1);
      }
      layer.scale.setValue(curScale);

      const rotationKeyframe = [];
      for (let j = 1; j <= layer.rotation.numKeys; j++) {
        const keyTime = layer.rotation.keyTime(j);
        const keyValue = layer.rotation.keyValue(j);
        rotationKeyframe.push({
          keyTime: keyTime,
          keyValue: keyValue
        });
      }
      const rotationKeyCount = layer.rotation.numKeys;
      const curRotation = layer.rotation.valueAtTime(curTime, true);
      for (let j = 0; j < rotationKeyCount; j++) {
        layer.rotation.removeKey(1);
      }
      layer.rotation.setValue(curRotation);

      const anchorPosList = [];
      for (let j = 1; j <= layer.position.numKeys; j++) {
        const keyTime = layer.position.keyTime(j);
        activeItem.time = keyTime;
        const anchorPos = layer.sourcePointToComp(targetAnchorPos);
        anchorPosList.push(anchorPos);
      }
      for (let j = 1; j <= layer.position.numKeys; j++) {
        const keyTime = layer.position.keyTime(j);
        const anchorPos = anchorPosList[j - 1];
        layer.position.setValueAtTime(keyTime, anchorPos);
      }
      layer.anchorPoint.setValue(targetAnchorPos);
      for (let j = 0; j < scaleKeyframe.length; j++) {
        const keyframe = scaleKeyframe[j];
        layer.scale.setValueAtTime(keyframe.keyTime, keyframe.keyValue);
      }
      for (let j = 0; j < rotationKeyframe.length; j++) {
        const keyframe = rotationKeyframe[j];
        layer.rotation.setValueAtTime(keyframe.keyTime, keyframe.keyValue);
      }

      activeItem.time = curTime;
    }
  }
  app.endUndoGroup();
}

function jump2HomeWeb() {
  alert(`开发中`);
  // try {
  //   system.callSystem("cmd.exe /c start https://www.jikeing.com/home");
  // } catch (e) {
  //   $.writeln(errString);
  // }
}

export function onPasteKeyframeEase() {
  var curSelectedKeys = getSelectedKey();
  pasteKeyframeEase(selectedKeys, curSelectedKeys);
}

function pasteKeyframeEase(srcKeys, destKeys) {
  if (!srcKeys) {
    alert(`请先复制关键帧`);
    return;
  }
  var keyFrameDataPairList = {
    srcFrameDataList: [],
    destFrameDataList: []
  };
  for (var prop in srcKeys) {
    for (var i = 0; i < srcKeys[prop].length; i++) {
      for (var j = 0; j < srcKeys[prop][i].values.length; j++) {
        keyFrameDataPairList.srcFrameDataList.push({
          keyframeIndex: srcKeys[prop][i].keyframeIndex[j],
          value: srcKeys[prop][i].values[j],
          inEase: srcKeys[prop][i].inEase[j],
          outEase: srcKeys[prop][i].outEase[j],
          prop: srcKeys[prop][i].prop
        });
      }
    }
  }
  for (var prop in destKeys) {
    for (var i = 0; i < destKeys[prop].length; i++) {
      destKeyframeCount += destKeys[prop][i].values.length;
      for (var j = 0; j < destKeys[prop][i].values.length; j++) {
        keyFrameDataPairList.destFrameDataList.push({
          keyframeIndex: destKeys[prop][i].keyframeIndex[j],
          value: destKeys[prop][i].values[j],
          inEase: destKeys[prop][i].inEase[j],
          outEase: destKeys[prop][i].outEase[j],
          prop: destKeys[prop][i].prop
        })
      }
    }
  }
  const srcKeyLength = keyFrameDataPairList.srcFrameDataList.length;
  const destKeyLength = keyFrameDataPairList.destFrameDataList.length;
  $.writeln(`源和目标关键帧数量：${srcKeyLength},${destKeyLength}`)
  if (destKeyLength % srcKeyLength !== 0) {
    alert(`复制粘贴的关键帧数量需要一致或者同倍数`);
    return;
  }
  app.beginUndoGroup(`粘贴关键帧速率`);

  var maxValue = 1;
  for (var index = 0; index < srcKeyLength; index++) {
    var srcFrameData = keyFrameDataPairList.srcFrameDataList[index];
    var inEase = srcFrameData.inEase;
    var outEase = srcFrameData.outEase;
    for (var j = 0; j < inEase.length; j++) {
      if (inEase[j].speed > maxValue)
        maxValue = inEase[j].speed;
      if (outEase[j].speed > maxValue)
        maxValue = outEase[j].speed;
    }
  }
  for (var index = 0; index < destKeyLength; index++) {
    var srcFrameData = keyFrameDataPairList.srcFrameDataList[index % srcKeyLength];
    var destFrameData = keyFrameDataPairList.destFrameDataList[index];
    var destProp = destFrameData.prop;
    var keyframeIndex = destFrameData.keyframeIndex;
    var inEase = srcFrameData.inEase;
    var outEase = srcFrameData.outEase;
    var destInEase = destFrameData.inEase;
    var destOutEase = destFrameData.outEase;

    var dim = destInEase.length;
    var easeInList = [];

    var easeOutList = [];
    for (var j = 0; j < dim; j++) {
      var newInEase = inEase[Math.min(j, inEase.length - 1)];
      var newOutEase = outEase[Math.min(j, outEase.length - 1)];
      var newInSpeed = newInEase.speed / maxValue * destInEase[Math.min(j, destInEase.length - 1)].speed;
      var newOutSpeed = newOutEase.speed / maxValue * destOutEase[Math.min(j, destOutEase.length - 1)].speed;

      easeInList.push(new KeyframeEase(newInSpeed, newInEase.influence));
      easeOutList.push(new KeyframeEase(newOutSpeed, newOutEase.influence));
    }
    destProp.setTemporalEaseAtKey(keyframeIndex, easeInList, easeOutList);

    var inInterp = srcFrameData.prop.keyInInterpolationType(keyframeIndex);
    var outInterp = srcFrameData.prop.keyOutInterpolationType(keyframeIndex);
    destProp.setInterpolationTypeAtKey(keyframeIndex, inInterp, outInterp);
  }
  app.endUndoGroup();
}


export function onPasteKeyframeValue() {
  var curSelectedKeys = getSelectedKey();
  pasteKeyValue(selectedKeys, curSelectedKeys);
}

function pasteKeyValue(srcKeys, destKeys) {
  if (!srcKeys) {
    alert(`请先复制关键帧`);
    return;
  }

  var srcKeyframeCount = 0;
  var destKeyframeCount = 0;
  for (var prop in srcKeys) {
    if (!destKeys[prop]) {
      alert(`复制粘贴的关键帧类型不对`);
      return;
    }
    for (var i = 0; i < srcKeys[prop].length; i++) {
      srcKeyframeCount += srcKeys[prop][i].values.length;
    }
  }
  for (var prop in destKeys) {
    for (var i = 0; i < destKeys[prop].length; i++) {
      destKeyframeCount += destKeys[prop][i].values.length;
    }
  }
  if (srcKeyframeCount != destKeyframeCount) {
    alert(`复制粘贴的关键帧数量不一致`);
    return;
  }
  app.beginUndoGroup(`粘贴关键帧值`);
  for (var propName in srcKeys) {
    var srcPropList = srcKeys[propName];
    var destPropList = destKeys[propName];
    var srcPropValueList = [];
    var destPropValueList = [];
    for (var i = 0; i < srcPropList.length; i++) {
      for (var j = 0; j < srcPropList[i].keyframeIndex.length; j++) {
        srcPropValueList.push({
          keyframeIndex: srcPropList[i].keyframeIndex[j],
          value: srcPropList[i].values[j],
          prop: srcPropList[i].prop
        });
      }
    }
    for (var i = 0; i < destPropList.length; i++) {
      for (var j = 0; j < destPropList[i].keyframeIndex.length; j++) {
        destPropValueList.push({
          keyframeIndex: destPropList[i].keyframeIndex[j],
          value: destPropList[i].values[j],
          prop: destPropList[i].prop
        })
      }
    }

    for (var i = 0; i < srcPropValueList.length; i++) {
      var srcPropValue = srcPropValueList[i];
      var destPropValue = destPropValueList[i];
      destPropValue.prop.setValueAtKey(destPropValue.keyframeIndex, srcPropValue.value);
    }
  }
  app.endUndoGroup();
}

/**
* 删除合并音频合成
**/
export function deleteMergeAudio() {
  $.writeln(mergeAudioCompItem);
  if (mergeAudioCompItem != null) {
    mergeAudioCompItem.remove();
    mergeAudioCompItem = null;
  }
}

/**
* 合并选中的音频到一个合成中，将合成复制到每个嵌套合成中
**/
export function copyAudioLayer2EmbedCompItem() {
  //遍历主图层，找到含有音频的层
  var activeItem = app.project.activeItem;
  var hasAudioLayer = [];
  if (activeItem != null && activeItem instanceof CompItem) {
    var activeComp = activeItem;
    var selectedLayers = activeComp.selectedLayers;
    for (var i = 0; i < selectedLayers.length; i++) {
      var curLayer = selectedLayers[i];
      if (curLayer.hasAudio) {
        hasAudioLayer.push(curLayer);
      }
    }
  }
  if (hasAudioLayer.length == 0) {
    alert(`请先选择有音频的图层`);
    return;
  }
  app.beginUndoGroup(`合成音频`);
  //将音频复制到一个新的合成中
  deleteMergeAudio();
  var newComp = app.project.items.addComp(
    `合成音频`,
    50,
    50,
    1.0,
    activeItem.duration,
    activeItem.frameRate
  );
  for (var i = 0; i < hasAudioLayer.length; i++) {
    var curLayer = hasAudioLayer[i];
    $.writeln(curLayer.name);
    curLayer.copyToComp(newComp);
  }
  //将新合成放置到每个合成中
  // var items = app.project.items;
  // for (var i = 1; i <= items.length; i++) {
  //   var curItem = items[i];
  //   if (!(curItem instanceof CompItem))
  //     continue;
  //   if (curItem === activeItem)
  //     continue;
  //   if (curItem === newComp)
  //     continue;
  //   var isInAudioCompose = false;
  //   for (var j = 0; j < hasAudioLayer.length; j++) {
  //     var audioLayer = hasAudioLayer[j];
  //     if (audioLayer.source === curItem) {
  //       isInAudioCompose = true;
  //       break;
  //     }
  //   }
  //   if (isInAudioCompose)
  //     continue;

  //   var layer = curItem.layers.add(newComp);
  //   layer.guideLayer = true;
  // }

  addAudioCompsToLayerComps(app.project.activeItem, newComp, 0)

  app.endUndoGroup();
  mergeAudioCompItem = newComp;
  $.writeln(mergeAudioCompItem);
}

const addAudioCompsToLayerComps = (item, audioComp, startTime) => {
  if (!(item instanceof CompItem)) {
    return;
  }
  if (item.id == audioComp.id) {
    return;
  }
  for (var i = 1; i <= item.numLayers; i++) {
    var layer = item.layer(i);
    if (layer.source && layer.source instanceof CompItem) {
      // 递归
      addAudioCompsToLayerComps(layer.source, audioComp, layer.startTime + startTime);

      let newLayer = layer.source.layers.add(audioComp);
      newLayer.startTime = -1 * layer.startTime;
      newLayer.guideLayer = true
      // newLayer.name = "8startTime" + newLayer.startTime
    }
  }
}

/**
     * 获取当前选中关键帧。保存值和缓动
     **/
function getSelectedKey() {
  var activeItem = app.project.activeItem;
  var selectedLayers = activeItem.selectedLayers;
  if (selectedLayers.length == 0) {
    alert(`需要先选中关键帧`)
    return null;
  }

  var prop2KeyList = {

  };

  var hasKey = false;
  for (var i = 0; i < selectedLayers.length; i++) {
    var layer = selectedLayers[i];
    var selectedProperties = layer.selectedProperties;
    for (var j = 0; j < selectedProperties.length; j++) {
      var property = selectedProperties[j];
      if (property.propertyType === PropertyType.PROPERTY) {
        var keysIndex = property.selectedKeys;
        if (keysIndex.length > 0) {
          if (!prop2KeyList[property.name]) {
            prop2KeyList[property.name] = [];
          }
          var propKeyFrame = {
            keyframeIndex: [],
            values: [],
            inEase: [],
            outEase: [],
            prop: property,
          };
          prop2KeyList[property.name].push(propKeyFrame);

          for (var k = 0; k < keysIndex.length; k++) {
            hasKey = true;
            var keyframeIndex = keysIndex[k];
            var value = property.keyValue(keyframeIndex);
            propKeyFrame.keyframeIndex.push(keyframeIndex);
            propKeyFrame.values.push(value);
            propKeyFrame.inEase.push(property.keyInTemporalEase(keyframeIndex));
            propKeyFrame.outEase.push(property.keyOutTemporalEase(keyframeIndex));
          }
        }
      }
    }
  }

  if (!hasKey) {
    return null;
  }
  return prop2KeyList;
}

export function onCopyKeyframe() {
  selectedKeys = getSelectedKey();
  if (selectedKeys == null) {
    alert(`当前没有选中关键帧`);
  } else {
    for (var key in selectedKeys) {
      var keyFrameData = selectedKeys[key];
      var count = 0;
      for (var i = 0; i < keyFrameData.length; i++) {
        count += keyFrameData[i].values.length;
        for (var j = 0; j < keyFrameData[i].values.length; j++) {
          var inEase = keyFrameData[i].inEase[j];
          var outEase = keyFrameData[i].outEase[j];
          var inEaseConcat = "";
          var outEaseConcat = "";
          for (var k = 0; k < inEase.length; k++) {
            var inEaseStr = "[" + inEase[k].speed + "," + inEase[k].influence + "]";
            var outEaseStr = "[" + outEase[k].speed + "," + outEase[k].influence + "]";
            inEaseConcat += inEaseStr;
            outEaseConcat += outEaseStr;
          }
          $.writeln("inEase" + j + ":" + inEaseConcat);
          $.writeln("outEase" + j + ":" + outEaseConcat);
        }
      }
      $.writeln(`已复制:${key},${count}个关键帧`);
    }
  }
}


/**
 * 
 * PSD 图层
 * 
 */

const isPSD = (fileName) => {
  let ext = fileName.slice(-4).toLowerCase()
  return ext === ".psd" || ext === ".psb";
  // return fileName.substring(fileName.length - suffix.length) === suffix;
}

// cache he comps with id
var compsMap = {}

// get layers of comps item
const getSubLayers = (item, level) => {

  // max level is 1
  if (level > 1 || !item) {
    return []
  }

  var layers = []

  // get the layer level
  if (item instanceof CompItem) {
    // cache
    compsMap[item.id] = item
    // get the item's layers
    for (var j = 1; j <= item.numLayers; j++) {
      var subLayer = item.layer(j)
      if (subLayer.source) {
        if (subLayer.source.typeName === `合成` || isPSD(subLayer.source.name)) {
          layers.push({
            id: subLayer.id,
            itemId: item.id,
            name: subLayer.source.typeName === `合成` ? subLayer.name : subLayer.source.name,
            type: subLayer.source.typeName,
            enabled: subLayer.enabled,
            expanded: false,
            show: true,
            layers: getSubLayers(subLayer.source, level + 1)
          })
        }
      }
    }
  }

  return layers
}

// 获取PSD合成下的所有layer信息
export const getLayersInPsdCompItem = () => {

  compsMap = {}

  if (!app.project
    || !app.project.activeItem
    || app.project.activeItem.selectedLayers.length != 1
    || !app.project.activeItem.selectedLayers[0].source
    || !(app.project.activeItem.selectedLayers[0].source instanceof CompItem)) {
    alert(`请先选择一个类型为"合成"的图层`);
    return;
  }

  var psdItem = app.project.activeItem.selectedLayers[0].source
  if (psdItem.layers.length === 0) {
    alert(`当前选择"合成"下没有图层内容`);
    return;
  }

  // cache
  compsMap[psdItem.id] = psdItem

  var psdLayerResult = {
    itemId: psdItem.id, // psd合成的ID
    layers: []
  }
  for (var i = 1; i <= psdItem.numLayers; i++) {
    var layer = psdItem.layer(i)
    // $.writeln(layer.name + ' source = ' + layer.source.typeName);

    if (layer.source) {
      if (layer.source.typeName === `合成` || isPSD(layer.source.name))
        psdLayerResult.layers.push({
          id: layer.id,
          itemId: psdItem.id,
          name: layer.source.typeName === `合成` ? layer.name : layer.source.name,
          type: layer.source.typeName,
          enabled: layer.enabled,
          expanded: false,
          show: true,
          layers: getSubLayers(layer.source, 0)
        })
    }
  }
  // alert('names = ' + JSON.stringify(psdLayerResult))

  return psdLayerResult
}

// 根据合成ID，将合成打开
export const getCompItem = (itemId) => {
  if (!app.project) {
    alert(`当前项目未打开`);
    return null;
  }

  var _targetItem = compsMap[itemId];

  if (!_targetItem) {
    alert(`当前传入合成ID无效`);
  }
  return _targetItem
}

// 根据合成Id, 图层Id, 获取图层对象
export const getPsdLayer = (itemId, layerId) => {
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
    alert(`当前传入图层ID无效`);
  }

  return { layer: _targetLayer, item: _targetItem }
}

// 根据选中合成下的某个Layer
export const selectPsdLayer = (itemId, layerId) => {
  var result = getPsdLayer(itemId, layerId);
  if (result.layer) {
    result.item.openInViewer();
    result.layer.selected = true
  }
}

// 将合成下的图层显示或隐藏
export const showTogglePsdLayer = (itemId, layerId, show) => {
  var result = getPsdLayer(itemId, layerId);
  if (result.layer) {
    result.layer.enabled = show
  }
}

/**
 * 
 * 骨骼
 * 
 */

// 
const getLayerSize = (layer) => {
  if (layer instanceof ShapeLayer) {
    return {
      width: app.project.activeItem.width,
      height: app.project.activeItem.height
    }
  } else {
    return layer.sourceRectAtTime(0, false)
  }
}

// 
const getLayerPins = (layer) => {
  let pins = []
  try {
    let pinPro = layer.effect("Puppet").property("arap").property(`网格`).property(`网格 1`).property(`变形`)
    let count = pinPro.numProperties
    for (var i = 1; i <= count; i++) {
      let pin = pinPro.property(i)
      pins.push({
        pin: pin,
        layer: layer
      })
    }
  } catch (err) {

  }

  return pins
}

// get selected pins in activeItem
const getSelectedPins = () => {
  var selectedProperties = app.project.activeItem.selectedProperties
  var num = selectedProperties.length
  var pins = []
  for (var i = 0; i < num; i++) {
    var p = selectedProperties[i]
    if (p.matchName === 'ADBE FreePin3 PosPin Atom') {
      pins.push(p)
    }
  }
  return pins
}

// create pin controllers with current layer
export const createControllers = () => {

  if (!app.project
    || !app.project.activeItem
    || app.project.activeItem.selectedLayers.length != 1) {
    alert(`请先选择一个已创建控制点的图层`);
    return;
  }

  let layer = app.project.activeItem.selectedLayers[0]

  var pins = getLayerPins(layer)
  if (pins.length === 0) {
    alert(`请先选择一个已创建控制点的图层`)
    return
  }

  app.beginUndoGroup("Create Controllers")

  _ = `获取当前合成中被选中的控制点`
  var selectedPins = getSelectedPins()
  if (selectedPins.length > 0) {
    var actualPins = [];
    for (var i = 0; i < pins.length; i++) {
      for (var j = 0; j < selectedPins.length; j++) {
        if (pins[i].pin == selectedPins[j]) {
          actualPins.push(pins[i]);
        }
      }
    }
    if (actualPins.length > 0) {
      pins = actualPins;
    }
  }

  for (var i = 0; i < pins.length; i++) {
    var pin = pins[i]
    var nullObj = app.project.activeItem.layers.addNull()
    nullObj.anchorPoint.setValue([50, 50])
    nullObj.scale.setValue([50, 50])
    nullObj.name = layer.name + " " + pin.pin.name
    // add Null Obj before current layer
    nullObj.moveBefore(layer)

    if (pin.layer instanceof AVLayer) {
      // let x = pin.pin.position.value[0] + (layer.position.value[0] - app.project.activeItem.width * 0.5)
      // let y = pin.pin.position.value[1] + (layer.position.value[1] - app.project.activeItem.height * 0.5)
      // nullObj.position.setValue([x, y])

      let scaleX = pin.layer.scale.value[0] / 100
      let scaleY = pin.layer.scale.value[1] / 100
      let screenW = app.project.activeItem.width
      let screenH = app.project.activeItem.height
      let layerWidth = layer.sourceRectAtTime(0, false).width
      let layerHeight = layer.sourceRectAtTime(0, false).height
      let x = pin.pin.position.value[0] * scaleX + (screenW * 0.5 - layerWidth * 0.5 * scaleX) + (pin.layer.position.value[0] - screenW * 0.5)
      let y = pin.pin.position.value[1] * scaleY + (screenH * 0.5 - layerHeight * 0.5 * scaleY) + (pin.layer.position.value[1] - screenH * 0.5)
      nullObj.position.setValue([x, y])

      pin.pin.position.expression = `
            fromComp(thisComp.layer("${nullObj.name}").toComp(thisComp.layer("${nullObj.name}").anchorPoint));
          `

    } else {
      let x = pin.pin.position.value[0]
      let y = pin.pin.position.value[1]
      nullObj.position.setValue([x, y])

      pin.pin.position.expression = `
        L = thisComp.layer("${nullObj.name}");
        if(L.hasParent)
        { 
        p = L.toComp(L.anchorPoint);
        p;
        } else {
        thisComp.layer("${nullObj.name}").transform.position
        }
      `
    }
  }
  app.endUndoGroup()
}


export const createIK = () => {

  if (!app.project
    || !app.project.activeItem
    || app.project.activeItem.selectedLayers.length != 3) {
    alert(`请先选择3个图层`);
    return;
  }

  let selectedLayers = app.project.activeItem.selectedLayers

  let nullObj1 = selectedLayers[0]
  let nullObj2 = selectedLayers[1]
  let nullObj3 = selectedLayers[2]

  app.beginUndoGroup("Create IK")

  var nullObj = app.project.activeItem.layers.addNull()
  nullObj.anchorPoint.setValue([50, 50])
  nullObj.scale.setValue([50, 50])
  nullObj.position.setValue([nullObj1.position.value[0], nullObj1.position.value[1]])
  nullObj.name = nullObj1.name + " IK Ctrl"
  // add checkbox effect
  nullObj.effect.addProperty('ADBE Checkbox Control').name = "IK Fild Direction"

  nullObj1.parent = nullObj2
  nullObj2.parent = nullObj3
  nullObj1.locked = true

  // add Null Obj before nullObj1
  nullObj.moveBefore(nullObj1)

  const expression = (upper) => `
      FLIP = thisComp.layer("${nullObj.name}").effect("IK Fild Direction")(1); 
      if(FLIP == true){cw = false;} 
      else {cw = true;} 
      upper = ${upper}; 
      upperLimb = "${nullObj3.name}"; 
      lowerLimb = "${nullObj2.name}"; 
      extremity = "${nullObj1.name}"; 
      effector = "${nullObj.name}"; 
      function getWorldPos(theLayerName){ 
      L = thisComp.layer(theLayerName); 
      return L.toWorld(L.anchorPoint); 
      } 
      A = getWorldPos(upperLimb); 
      B = getWorldPos(lowerLimb); 
      C = getWorldPos(extremity); 
      E = getWorldPos(effector); 
      a = length(B,C); 
      b = length(E,A); 
      c = length(A,B); 
      x = (b*b + c*c - a*a )/(2*b); 
      alpha = Math.acos(clamp(x/c,-1,1)); 
      if (upper){ 
      D = E - A; 
      delta = Math.atan2(D[1],D[0]); 
      result = radiansToDegrees(delta - (cw ? 1 : -1)*alpha); 
      V = B - A; 
      adj1 = radiansToDegrees(Math.atan2(V[1],V[0])); 
      result - adj1 + value; 
      }else{ 
      y = b - x; 
      gamma = Math.acos(clamp(y/a,-1,1)); 
      result = (cw ? 1 : -1)*radiansToDegrees(gamma + alpha); 
      V1 = B - A; 
      adj1 = radiansToDegrees(Math.atan2(V1[1],V1[0])); 
      V2 = C - B; 
      adj2 = radiansToDegrees(Math.atan2(V2[1],V2[0])); 
      result +  adj1 - adj2 + value; 
      } 
    `

  nullObj3.transform.rotation.expression = expression(true)
  nullObj2.transform.rotation.expression = expression(false)

  app.endUndoGroup()
}

// Add Null Object
export const addNullObj = () => {

  var selectedLayers = app.project.activeItem.selectedLayers

  function createNullObj() {
    var nullObj = app.project.activeItem.layers.addNull()
    // green color
    nullObj.label = 9
    nullObj.anchorPoint.setValue([50, 50])
    return nullObj
  }

  app.beginUndoGroup(`添加空对象`)

  if (selectedLayers.length == 0) {
    _ = `没选中的图层，则直接添加最顶层上`
    _ = createNullObj()

  } else if (selectedLayers.length > 1) { 
     var nullObj = createNullObj();
    nullObj.name = '新创建的空对象'

    let topLayer = selectedLayers[0]
    nullObj.moveBefore(topLayer)
    nullObj.startTime = topLayer.startTime
    nullObj.inPoint = topLayer.inPoint
    nullObj.outPoint = topLayer.outPoint
    nullObj.position.setValue([topLayer.position.value[0], topLayer.position.value[1]])

    for (let i = 0; i < selectedLayers.length; i++) {
        let layer = selectedLayers[i]
        layer.parent = nullObj
    }

  } else {

    var nullObj = createNullObj();
    var layer = selectedLayers[0];
    nullObj.name = layer.name + ' 的空对象1'

    nullObj.moveBefore(layer)
    nullObj.startTime = layer.startTime
    nullObj.inPoint = layer.inPoint
    nullObj.outPoint = layer.outPoint
    nullObj.position.setValue([layer.position.value[0], layer.position.value[1]])

    layer.parent = nullObj
  }

  app.endUndoGroup()

}

// 统一的路径处理函数
export const joinPath = (parts: string[]) => {
    // var parts = Array.prototype.slice.call(arguments);
    var separator = (File.fs == "Windows") ? "\\" : "/";
    return parts.join(separator).replace(/\\/g, separator);
}