package org.bspb.smartbirds.pro.tools

import com.google.gson.JsonObject
import okhttp3.mockwebserver.MockResponse
import org.bspb.smartbirds.pro.backend.dto.*

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

        fun prepareMapLayersResponse(): MockResponse {
            var parser = SBGsonParser.createParser()
            var mapLayersResponse = ResponseListEnvelope<MapLayerItem>()
            mapLayersResponse.count = 0
            mapLayersResponse.data = ArrayList()
            return MockResponse().setBody(parser.toJson(mapLayersResponse))
        }

        fun prepareUploadFileResponse(): MockResponse {
            var parser = SBGsonParser.createParser()
            var uploadResponse = ResponseEnvelope<FileId>()
            uploadResponse.data = FileId()
            return MockResponse().setBody(parser.toJson(uploadResponse))
        }

        fun prepareUploadFormRespons(): MockResponse {
            var parser = SBGsonParser.createParser()
            var uploadResponse = UploadFormResponse()
            uploadResponse.data = JsonObject()
            return MockResponse().setBody(parser.toJson(uploadResponse))
        }
    }
}