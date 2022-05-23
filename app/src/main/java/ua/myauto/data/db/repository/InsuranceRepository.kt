package ua.myauto.data.db.repository

import java.lang.Exception
import ua.myauto.App
import ua.myauto.data.db.entities.insurance.InsuranceReportsDataEntity
import ua.myauto.data.db.entities.insurance.InsuranceUsersDataEntity
import ua.myauto.domain.mappers.toInsurance
import ua.myauto.domain.mappers.toInsuranceEntity
import ua.myauto.domain.mappers.toReport
import ua.myauto.domain.mappers.toReportEntity
import ua.myauto.domain.models.User
import ua.myauto.domain.models.insurance.Insurance
import ua.myauto.domain.models.report.InsuranceReport
import ua.myauto.utils.ONE_DAY_MILLIS

class InsuranceRepository(application: App, private val user: User) {
    private val insuranceDao = application.database.insuranceDao()

    suspend fun getInsurances(): List<Insurance> =
        insuranceDao.getUserInsurances(user.username).map { it.toInsurance() }

    suspend fun getAllReports(): List<InsuranceReport> {
        val insurances = getInsurances()
        val reports = mutableListOf<InsuranceReport>()

        insurances.forEach {
            reports.addAll(getInsuranceReportsFor(it))
        }

        return reports
    }

    suspend fun getInsuranceReportsFor(insurance: Insurance): List<InsuranceReport> =
        insuranceDao.getAllInsuranceReports(insurance.id).map { it.toReport() }

    suspend fun isInsuranceExistsFor(carNumber: String): Boolean =
        insuranceDao.getInsuranceBy(carNumber) != null

    suspend fun addInsurance(insurance: Insurance) {
        try {
            val insuranceEntity = insurance.toInsuranceEntity()
            insuranceDao.insertInsurance(insuranceEntity)
            insuranceDao.insertInsuranceUsersData(
                InsuranceUsersDataEntity(
                    insuranceId = insurance.id,
                    insuranceUsername = user.username
                )
            )
        } catch (ex: Exception) {
            ex.printStackTrace()
        }
    }

    suspend fun setInsurancePaid(insurance: Insurance) {
        insuranceDao.updateInsurance(insurance.toInsuranceEntity())
    }

    suspend fun addInsuranceReport(insuranceReport: InsuranceReport) {
        try {
            val insuranceReportEntity = insuranceReport.toReportEntity()
            insuranceDao.insertInsuranceReport(insuranceReportEntity)
            insuranceDao.insertInsuranceReportUsersData(
                InsuranceReportsDataEntity(
                    insuranceId = insuranceReport.insuranceId,
                    reportId = insuranceReportEntity.id
                )
            )
        } catch (ex: Exception) {
            ex.printStackTrace()
        }
    }

    suspend fun deleteInsurance(insurance: Insurance) =
        insuranceDao.deleteInsurance(insurance.toInsuranceEntity())

    suspend fun deleteUserInsurances() {
        val insurances = getInsurances()
        insurances.forEach {
            deleteInsurance(it)
        }
    }

    suspend fun deleteUserReports() {
        val insurances = getInsurances()
        insurances.forEach {
            val reports = getInsuranceReportsFor(it)
            reports.forEach { report ->
                insuranceDao.deleteInsuranceReport(report.toReportEntity())
            }
        }
    }

    companion object {
        fun newInstance(user: User) = InsuranceRepository(App.instance, user)
    }
}