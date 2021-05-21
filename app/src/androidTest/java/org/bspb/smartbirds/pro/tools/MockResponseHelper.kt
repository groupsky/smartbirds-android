package org.bspb.smartbirds.pro.tools

import okhttp3.mockwebserver.MockResponse
import org.bspb.smartbirds.pro.backend.dto.LoginResponse
import org.bspb.smartbirds.pro.backend.dto.Nomenclature
import org.bspb.smartbirds.pro.backend.dto.ResponseListEnvelope
import org.bspb.smartbirds.pro.backend.dto.User

class MockResponseHelper {

    companion object {
        fun prepareSuccessLoginResponse(): MockResponse {
            var parser = SBGsonParser.createParser()
            var loginResponse = LoginResponse()
            loginResponse.success = true
            loginResponse.token = "token"
            loginResponse.user = User()
            return MockResponse().setBody(parser.toJson(loginResponse))
        }

        fun prepareNomenclatureResponse(): MockResponse {
            var parser = SBGsonParser.createParser()
            var nomenclaturesResponse = ResponseListEnvelope<Nomenclature>()
            nomenclaturesResponse.count = 0
            nomenclaturesResponse.data = ArrayList()
            return MockResponse().setBody(parser.toJson(nomenclaturesResponse))
        }
    }
}