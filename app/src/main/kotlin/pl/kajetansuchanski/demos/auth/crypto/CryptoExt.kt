package pl.kajetansuchanski.demos.auth.crypto

import android.util.Base64

inline fun ByteArray.encodeBase64(): ByteArray = Base64.encode(this, Base64.DEFAULT)
inline fun ByteArray.decodeBase64(): ByteArray = Base64.decode(this, Base64.DEFAULT)