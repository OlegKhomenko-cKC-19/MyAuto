package ua.myauto.data.db.dao

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.Query
import androidx.room.Update
import ua.myauto.data.db.entities.insurance.InsuranceEntity
import ua.myauto.data.db.entities.insurance.InsuranceReportEntity
import ua.myauto.data.db.entities.insurance.InsuranceReportsDataEntity
import ua.myauto.data.db.entities.insurance.InsuranceUsersDataEntity

@Dao
interface InsuranceDao {

    @Query("SELECT Insurances.* FROM Insurances INNER JOIN Insurances_Users ON Insurances_Users.insuranceId = Insurances.id WHERE Insurances_Users.insuranceUsername = :username")
    suspend fun getUserInsurances(username: String): List<InsuranceEntity>

    @Query("SELECT * FROM Insurances")
    suspend fun getAllInsurances(): List<InsuranceEntity>

    @Query("SELECT * FROM Insurances_Users")
    suspend fun getAllDop(): List<InsuranceUsersDataEntity>

    @Query("SELECT InsuranceReports.* FROM InsuranceReports INNER JOIN Insurances_InsuranceReports ON InsuranceReports.id = Insurances_InsuranceReports.reportId WHERE Insurances_InsuranceReports.insuranceId = :id")
    suspend fun getAllInsuranceReports(id: Int): List<InsuranceReportEntity>

    @Query("SELECT * FROM Insurances WHERE carNumber = :carNumber")
    suspend fun getInsuranceBy(carNumber: String): InsuranceEntity?

    @Update
    suspend fun updateInsurance(insuranceEntity: InsuranceEntity)

    @Insert
    suspend fun insertInsurance(insuranceEntity: InsuranceEntity): Long

    @Insert
    suspend fun insertInsuranceUsersData(insuranceUsersData: InsuranceUsersDataEntity)

    @Insert
    suspend fun insertInsuranceReport(insuranceReportEntity: InsuranceReportEntity)

    @Insert
    suspend fun insertInsuranceReportUsersData(insuranceReportsData: InsuranceReportsDataEntity)

    @Delete
    suspend fun deleteInsurance(insuranceEntity: InsuranceEntity)

    @Delete
    suspend fun deleteInsuranceReport(insuranceReportEntity: InsuranceReportEntity)
}
