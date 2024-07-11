/*
 * Copyright 2024 HM Revenue & Customs
 *
 */

package uk.gov.hmrc.perftests.reauthJourneys

import com.nimbusds.jose.crypto.ECDSASigner
import com.nimbusds.jose.util.JSONObjectUtils
import com.nimbusds.jose.{JWSAlgorithm, JWSHeader}
import com.nimbusds.jwt.{JWTClaimsSet, SignedJWT}
import uk.gov.hmrc.perftests.reauthJourneys.common.AppConfig.oneLoginStubUrl

import java.security.KeyFactory
import java.security.interfaces.ECPrivateKey
import java.security.spec.PKCS8EncodedKeySpec
import java.util.{Base64, Calendar, Date}

object JwtHelper {

  //A hard coded jwt id
  val JTI = "jtibWJq"

  //Decode and get the key
  val stubPrivateKey: String = "MEECAQAwEwYHKoZIzj0CAQYIKoZIzj0DAQcEJzAlAgEBBCCNFhGO3F9Lzm2tGry7XwkYqpbv2yQvq+FqMVTNyreCWQ=="

  //A normal and standard way to create a ECPrivateKey, check the bouncycastle lib for more details.
  val privateSigningKey: ECPrivateKey = {
    KeyFactory.getInstance("EC")
      .generatePrivate(
        new PKCS8EncodedKeySpec(
          Base64.getDecoder.decode(stubPrivateKey))
      ).asInstanceOf[ECPrivateKey]
  }

  def now: Date = new Date()

  def addMinutesToDate(date: Date, minutes: Int): Date = {
    val calendar = Calendar.getInstance
    calendar.setTime(date)
    calendar.add(Calendar.MINUTE, minutes)
    calendar.getTime
  }

  def expiry: Date = addMinutesToDate(now, 60) //one hour

  def createAValidLogoutToken(subId: String): String = {

    val jwt: SignedJWT = new SignedJWT(
      new JWSHeader.Builder(JWSAlgorithm.ES256).build(),
      new JWTClaimsSet.Builder()
        .issuer(s"${oneLoginStubUrl}/")
        .subject(subId)
        .audience("one-login-gateway")
        .expirationTime(expiry)
        .issueTime(now)
        .claim("events", JSONObjectUtils.parse(s"""{"http://schemas.openid.net/event/backchannel-logout": {}}"""))
        .claim("jti", JTI)
        .build()
    )

    jwt.sign(new ECDSASigner(privateSigningKey))

    jwt.serialize()
  }

}
