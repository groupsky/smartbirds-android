package org.bspb.smartbirds.pro.utils

import android.content.Context
import org.bspb.smartbirds.pro.R
import org.bspb.smartbirds.pro.backend.dto.Nomenclature
import org.bspb.smartbirds.pro.tools.SBGsonParser

class NomenclaturesHelper {

    companion object {
        fun convertDtoToEntity(dto: Nomenclature): org.bspb.smartbirds.pro.room.Nomenclature {
            return org.bspb.smartbirds.pro.room.Nomenclature(
                0,
                dto.type,
                SBGsonParser.createParser().toJson(dto).toByteArray(Charsets.UTF_8)
            )
        }

        fun convertSpeciesDtoToEntity(
            dto: Nomenclature,
            context: Context
        ): org.bspb.smartbirds.pro.room.Nomenclature {
            var speciesDto = Nomenclature.fromSpecies(dto, context.getString(R.string.locale))
            return org.bspb.smartbirds.pro.room.Nomenclature(
                0,
                speciesDto.type,
                SBGsonParser.createParser().toJson(speciesDto).toByteArray(Charsets.UTF_8)
            )
        }
    }

}