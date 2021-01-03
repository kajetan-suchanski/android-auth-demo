package pl.kajetansuchanski.demos.auth

typealias OnDeviceAuthResult = (authorized: Boolean) -> Unit

interface DeviceAuthFragmentInterface {
    var onDeviceAuthResult: OnDeviceAuthResult?
}