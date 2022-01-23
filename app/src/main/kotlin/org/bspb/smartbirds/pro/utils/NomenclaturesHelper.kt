package org.bspb.smartbirds.pro.utils

import android.content.Context
import org.bspb.smartbirds.pro.R
import org.bspb.smartbirds.pro.backend.dto.Nomenclature
import org.bspb.smartbirds.pro.room.NomenclatureModel
import org.bspb.smartbirds.pro.tools.SBGsonParser

class NomenclaturesHelper {

    companion object {
        fun convertDtoToEntity(dto: Nomenclature): NomenclatureModel {
            return NomenclatureModel(
                0,
                dto.type,
                SBGsonParser.createParser().toJson(dto).toByteArray(Charsets.UTF_8)
            )
        }

        fun convertSpeciesDtoToEntity(
            dto: Nomenclature,
            context: Context
        ): NomenclatureModel {
            var speciesDto = Nomenclature.fromSpecies(dto, context.getString(R.string.locale))
            return NomenclatureModel(
                0,
                speciesDto.type,
                SBGsonParser.createParser().toJson(speciesDto).toByteArray(Charsets.UTF_8)
            )
        }
    }

}