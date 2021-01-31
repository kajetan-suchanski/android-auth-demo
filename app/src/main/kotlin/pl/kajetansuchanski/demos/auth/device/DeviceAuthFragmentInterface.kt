package pl.kajetansuchanski.demos.auth.device

typealias OnDeviceAuthResult = (authorized: Boolean) -> Unit

interface DeviceAuthFragmentInterface {
    var onDeviceAuthResult: OnDeviceAuthResult?
}