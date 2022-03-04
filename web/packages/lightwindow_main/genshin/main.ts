import { ArcRotateCamera, Engine, HemisphericLight, Scene, SceneLoader, Vector3 } from 'babylonjs'
import 'babylonjs-loaders/babylonjs.loaders.min.js'

const canvas = document.getElementById('canvas') as HTMLCanvasElement
const engine = new Engine(canvas)
engine.loadingScreen = { displayLoadingUI() { }, hideLoadingUI() { }, loadingUIBackgroundColor: '', loadingUIText: '' }

async function createScene() {
  const scene = new Scene(engine)
  scene.useRightHandedSystem = true
  scene.createDefaultCamera(true, true)
  scene.createDefaultLight(true)
  const camera = scene.cameras[0] as ArcRotateCamera
  const light = scene.lights[0] as HemisphericLight
  // disable moving
  // camera.panningSensibility = 0
  // disable zooming
  // camera.lowerRadiusLimit = 1
  // camera.upperRadiusLimit = 1

  scene.clearColor.set(0, 0, 0, 0)
  camera.setPosition(new Vector3(0, 0.4, 1))
  camera.setTarget(new Vector3(0, 0.4, 0))
  await SceneLoader.AppendAsync('/model/', 'paimeng.glb', scene)
  scene.freezeMaterials()
  return scene
}

createScene().then(scene => {
  engine.runRenderLoop(() => scene.render())
})

const resize = () => engine.resize()
window.addEventListener('resize', resize)
