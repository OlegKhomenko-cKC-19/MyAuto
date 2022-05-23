package ua.myauto.data.db.converters

import androidx.room.TypeConverter
import com.google.gson.GsonBuilder
import com.google.gson.typeadapters.RuntimeTypeAdapterFactory
import ua.myauto.domain.models.report.ReportRiskType
import ua.myauto.domain.models.report.ReportRiskType.Animals
import ua.myauto.domain.models.report.ReportRiskType.Crash
import ua.myauto.domain.models.report.ReportRiskType.Disaster
import ua.myauto.domain.models.report.ReportRiskType.PDTO

class ReportRiskTypeConverter {
    companion object {
        const val TYPE_FIELD = "riskType"
    }

    private val runtimeTypeAdapterFactory
        get() = RuntimeTypeAdapterFactory
            .of(ReportRiskType::class.java, TYPE_FIELD)
            .registerSubtype(PDTO::class.java, PDTO::class.java.name)
            .registerSubtype(Animals::class.java, Animals::class.java.name)
            .registerSubtype(Disaster::class.java, Disaster::class.java.name)
            .registerSubtype(Crash::class.java, Crash::class.java.name)

    private val gson
        get() = GsonBuilder().registerTypeAdapterFactory(runtimeTypeAdapterFactory).create()

    @TypeConverter
    fun serialize(type: ReportRiskType): String =
        gson.toJson(
            when (type) {
                is PDTO -> type
                is Animals -> type
                is Disaster -> type
                is Crash -> type
            }.apply {
                riskType = type::class.java.name
            }
        )


    @TypeConverter
    fun deserialize(typeJSON: String): ReportRiskType =
        gson.fromJson(typeJSON, ReportRiskType::class.java).apply {
            riskType = this::class.java.name
        }
}