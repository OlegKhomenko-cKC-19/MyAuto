
package ua.myauto.data.firebase

import android.util.Log
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.ktx.Firebase
import com.google.gson.Gson
import com.google.gson.GsonBuilder
import com.google.gson.typeadapters.RuntimeTypeAdapterFactory
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import ua.myauto.data.db.converters.ReportRiskTypeConverter
import ua.myauto.data.db.repository.ExpenseRepository
import ua.myauto.data.db.repository.InsuranceRepository
import ua.myauto.data.local.LocalRepository
import ua.myauto.domain.models.User
import ua.myauto.domain.models.expense.Expense
import ua.myauto.domain.models.expense.ExpenseCategory
import ua.myauto.domain.models.insurance.Insurance
import ua.myauto.domain.models.insurance.InsuranceType
import ua.myauto.domain.models.report.InsuranceReport
import ua.myauto.domain.models.report.ReportRiskType
import ua.myauto.domain.models.report.ReportRiskType.Animals
import ua.myauto.domain.models.report.ReportRiskType.Crash
import ua.myauto.domain.models.report.ReportRiskType.Disaster
import ua.myauto.domain.models.report.ReportRiskType.PDTO
import ua.myauto.utils.NetworkUtils

object RemoteRepository {

    private val runtimeTypeAdapterFactory
        get() = RuntimeTypeAdapterFactory
            .of(ReportRiskType::class.java, ReportRiskTypeConverter.TYPE_FIELD)
            .registerSubtype(PDTO::class.java, PDTO::class.java.name)
            .registerSubtype(Animals::class.java, Animals::class.java.name)
            .registerSubtype(Disaster::class.java, Disaster::class.java.name)
            .registerSubtype(Crash::class.java, Crash::class.java.name)

    private val gson
        get() = GsonBuilder().registerTypeAdapterFactory(runtimeTypeAdapterFactory).create()

    private val database get() = Firebase.firestore
    private val userCollection get() = database.collection("Users")
    private val expensesCollection get() = database.collection("Expenses")
    private val insurancesCollection get() = database.collection("Insurances")
    private val insuranceReportsCollection get() = database.collection("InsuranceReports")

    fun isUserExists(username: String, onCheckResult: (Boolean) -> Unit) {
        if (!NetworkUtils.isNetworkConnected) {
            onCheckResult(true)
            return
        }

        userCollection
            .document(username)
            .get()
            .addOnSuccessListener { onCheckResult(it.exists()) }
            .addOnFailureListener { onCheckResult(true) }
    }

    fun registerUser(user: User, onUserRegistered: (Boolean) -> Unit) {
        if (!NetworkUtils.isNetworkConnected) {
            onUserRegistered(false)
            return
        }

        userCollection
            .document(user.username)
            .set(user.toFirebaseModel())
            .addOnSuccessListener { onUserRegistered(true) }
            .addOnFailureListener { onUserRegistered(false) }
    }

    fun loginUser(username: String, password: String, onLoginResult: (User?) -> Unit) {
        if (!NetworkUtils.isNetworkConnected) {
            onLoginResult(null)
            return
        }

        userCollection.document(username)
            .get()
            .addOnSuccessListener { result ->
                if (password == result["password"].toString()) {
                    val user = result.data?.toUser()
                    if (user == null) {
                        onLoginResult(null)
                        return@addOnSuccessListener
                    }

                    onLoginResult(user)
                } else {
                    onLoginResult(null)
                }
            }
            .addOnFailureListener {
                onLoginResult(null)
            }
    }

    fun isInsuranceExists(carNumber: String, onCheckResult: (Boolean) -> Unit) {
        if (!NetworkUtils.isNetworkConnected) {
            onCheckResult(true)
            return
        }

        insurancesCollection.whereEqualTo("carNumber", carNumber).get()
            .addOnSuccessListener { onCheckResult(!it.isEmpty) }
            .addOnFailureListener { onCheckResult(true) }
    }

    fun addInsurance(insurance: Insurance, onInsuranceAdded: (Boolean) -> Unit) {
        if (!NetworkUtils.isNetworkConnected) {
            onInsuranceAdded(false)
            return
        }

        insurancesCollection.add(insurance.toFirebaseModel())
            .addOnSuccessListener { onInsuranceAdded(true) }
            .addOnFailureListener { onInsuranceAdded(false) }
    }

    fun updateInsurance(insurance: Insurance, onInsuranceUpdated: () -> Unit) {
        if (!NetworkUtils.isNetworkConnected) {
            onInsuranceUpdated()
            return
        }

        insurancesCollection.whereEqualTo("username", LocalRepository.username)
            .whereEqualTo("id", insurance.id)
            .get()
            .addOnSuccessListener {
                if (!it.isEmpty) {
                    val doc = it.documents.first()
                    doc.reference.update(insurance.toFirebaseModel())
                        .addOnCompleteListener {
                            onInsuranceUpdated()
                        }
                } else {
                    onInsuranceUpdated()
                }
            }
    }

    fun removeInsurance(insurance: Insurance, onInsuranceRemoved: () -> Unit) {
        if (!NetworkUtils.isNetworkConnected) {
            onInsuranceRemoved()
            return
        }

        insurancesCollection
            .whereEqualTo("username", LocalRepository.username)
            .whereEqualTo("id", insurance.id)
            .get()
            .addOnSuccessListener {
                it.documents.firstOrNull()?.reference?.delete()
                onInsuranceRemoved()
            }
            .addOnFailureListener {
                onInsuranceRemoved()
            }
    }

    fun addInsuranceReport(insuranceReport: InsuranceReport, onReportAdded: (Boolean) -> Unit) {
        if (!NetworkUtils.isNetworkConnected) {
            onReportAdded(false)
            return
        }

        insuranceReportsCollection.add(insuranceReport.toFirebaseModel())
            .addOnSuccessListener { onReportAdded(true) }
            .addOnFailureListener { onReportAdded(false) }
    }

    fun addExpense(expense: Expense, onExpenseAdded: (Boolean) -> Unit) {
        if (!NetworkUtils.isNetworkConnected) {
            onExpenseAdded(false)
            return
        }

        expensesCollection.add(expense.toFirebaseModel())
            .addOnSuccessListener { onExpenseAdded(true) }
            .addOnFailureListener { onExpenseAdded(false) }
    }

    fun removeExpense(expense: Expense, onExpenseRemoved: () -> Unit) {
        if (!NetworkUtils.isNetworkConnected) {
            onExpenseRemoved()
            return
        }

        expensesCollection
            .whereEqualTo("username", LocalRepository.username)
            .whereEqualTo("id", expense.id)
            .get()
            .addOnSuccessListener {
                it.documents.firstOrNull()?.reference?.delete()
                onExpenseRemoved()
            }
            .addOnFailureListener {
                onExpenseRemoved()
            }
    }

    fun mergeAllUserData(user: User, onDataMerged: () -> Unit) {
        if (!NetworkUtils.isNetworkConnected) {
            onDataMerged()
            return
        }

        mergeInsurances(user) {
            mergeInsuranceReports(user) {
                mergeExpenses(user, onDataMerged)
            }
        }
    }

    private fun mergeInsurances(user: User, onInsurancesMerged: () -> Unit) {
        val insuranceRepository = InsuranceRepository.newInstance(user)
        val insuranceToSaveLocal = mutableListOf<Insurance>()
        val insuranceToUploadBatch = database.batch()

        GlobalScope.launch {
            val storedInsurances = insuranceRepository.getInsurances()
            insurancesCollection.whereEqualTo("username", user.username).get()
                .addOnSuccessListener { queryResult ->
                    if (queryResult.isEmpty) {
                        onInsurancesMerged()
                        return@addOnSuccessListener
                    }

                    val remoteInsurances = queryResult.map { doc -> doc.data.toInsurance() }

                    remoteInsurances.forEach { remoteInsurance ->
                        if (!storedInsurances.contains(remoteInsurance)) {
                            insuranceToSaveLocal.add(remoteInsurance)
                        }
                    }

                    storedInsurances.forEach { storedInsurance ->
                        if (!remoteInsurances.contains(storedInsurance)) {
                            insuranceToUploadBatch.set(
                                insurancesCollection.document(),
                                storedInsurance.toFirebaseModel()
                            )
                        }
                    }

                    fun saveInsurances(onSaved: () -> Unit) {
                        GlobalScope.launch {
                            insuranceToSaveLocal.forEach {
                                insuranceRepository.addInsurance(it)
                            }

                            withContext(Dispatchers.Main) {
                                onSaved()
                            }
                        }
                    }

                    fun uploadInsurances(onUploaded: () -> Unit) {
                        insuranceToUploadBatch.commit().addOnCompleteListener { onUploaded() }
                    }

                    saveInsurances { uploadInsurances(onInsurancesMerged) }
                }
                .addOnFailureListener {
                    onInsurancesMerged()
                }
        }
    }

    private fun mergeInsuranceReports(user: User, onInsuranceReportsMerged: () -> Unit) {
        val insuranceRepository = InsuranceRepository.newInstance(user)
        val insuranceReportsToSaveLocal = mutableListOf<InsuranceReport>()
        val insuranceReportsToUploadBatch = database.batch()

        GlobalScope.launch {
            val storedReports = insuranceRepository.getAllReports()
            insuranceReportsCollection.whereEqualTo("username", user.username).get()
                .addOnSuccessListener { queryResult ->
                    if (queryResult.isEmpty) {
                        onInsuranceReportsMerged()
                        return@addOnSuccessListener
                    }

                    val remoteReports = queryResult.map { doc -> doc.data.toInsuranceReport() }

                    remoteReports.forEach { remoteReport ->
                        if (storedReports.all { it.id != remoteReport.id }) {
                            insuranceReportsToSaveLocal.add(remoteReport)
                        }
                    }

                    storedReports.forEach { storedReport ->
                        if (remoteReports.all { it.id != storedReport.id }) {
                            insuranceReportsToUploadBatch.set(
                                insuranceReportsCollection.document(),
                                storedReport.toFirebaseModel()
                            )
                        }
                    }

                    fun saveInsurancesReports(onSaved: () -> Unit) {
                        GlobalScope.launch {
                            insuranceReportsToSaveLocal.forEach {
                                insuranceRepository.addInsuranceReport(it)
                            }

                            withContext(Dispatchers.Main) {
                                onSaved()
                            }
                        }
                    }

                    fun uploadInsuranceReports(onUploaded: () -> Unit) {
                        insuranceReportsToUploadBatch.commit()
                            .addOnCompleteListener {
                                onUploaded()
                            }
                    }

                    saveInsurancesReports { uploadInsuranceReports(onInsuranceReportsMerged) }
                }
                .addOnFailureListener {
                    onInsuranceReportsMerged()
                }
        }
    }

    private fun mergeExpenses(user: User, onExpensesMerged: () -> Unit) {
        val expenseRepository = ExpenseRepository.newInstance(user)
        val expensesToSaveLocal = mutableListOf<Expense>()
        val expensesToUploadBatch = database.batch()

        GlobalScope.launch {
            val storedExpenses = expenseRepository.getAllExpenses()
            expensesCollection.whereEqualTo("username", user.username).get()
                .addOnSuccessListener { queryResult ->
                    if (queryResult.isEmpty) {
                        onExpensesMerged()
                        return@addOnSuccessListener
                    }

                    val remoteExpenses = queryResult.map { doc -> doc.data.toExpense() }

                    remoteExpenses.forEach { remoteExpense ->
                        if (!storedExpenses.contains(remoteExpense)) {
                            expensesToSaveLocal.add(remoteExpense)
                        }
                    }

                    storedExpenses.forEach { storedExpense ->
                        if (!remoteExpenses.contains(storedExpense)) {
                            expensesToUploadBatch.set(
                                expensesCollection.document(),
                                storedExpense.toFirebaseModel()
                            )
                        }
                    }

                    //save all local
                    fun saveExpenses(onSaved: () -> Unit) {
                        GlobalScope.launch {
                            expensesToSaveLocal.forEach {
                                expenseRepository.addExpense(it)
                            }

                            withContext(Dispatchers.Main) {
                                onSaved()
                            }
                        }
                    }

                    fun uploadExpenses(onUploaded: () -> Unit) {
                        expensesToUploadBatch.commit().addOnCompleteListener { onUploaded() }
                    }

                    saveExpenses { uploadExpenses(onExpensesMerged) }
                }
                .addOnFailureListener {
                    onExpensesMerged()
                }
        }
    }

    private fun User.toFirebaseModel() = hashMapOf(
        "username" to username,
        "email" to email,
        "password" to password,
        "firstName" to firstName,
        "lastName" to lastName
    )

    private fun Map<String, Any>.toUser() = User(
        username = this["username"] as String,
        email = this["email"] as String,
        password = this["password"] as String,
        firstName = this["firstName"] as String,
        lastName = this["lastName"] as String,
    )

    private fun Insurance.toFirebaseModel() = hashMapOf(
        "username" to LocalRepository.username,
        "id" to id,
        "number" to number,
        "serial" to serial,
        "typeId" to type.ordinal,
        "price" to price,
        "issuedAt" to issuedAt,
        "expiresAt" to expiresAt,
        "carNumber" to carNumber
    )

    private fun Map<String, Any>.toInsurance() = Insurance(
        id = this["id"].toString().toInt(),
        number = this["number"].toString().toInt(),
        serial = this["serial"] as String,
        type = InsuranceType.by(this["typeId"].toString().toInt()),
        price = this["price"].toString().toFloat(),
        issuedAt = this["issuedAt"] as Long,
        expiresAt = this["expiresAt"] as Long,
        carNumber = this["carNumber"] as String
    )

    private fun InsuranceReport.toFirebaseModel() = hashMapOf(
        "username" to LocalRepository.username,
        "id" to id,
        "number" to number,
        "insuranceId" to insuranceId,
        "insuranceNumber" to insuranceNumber,
        "insuranceSerial" to insuranceSerial,
        "issuedAt" to issuedAt,
        "fullName" to fullName,
        "phoneNumber" to phoneNumber,
        "email" to email,
        "registrationAddress" to registrationAddress,
        "liveAddress" to liveAddress,
        "reportRiskType" to gson.toJson(reportRiskType),
        "dateTime" to dateTime,
        "carNumber" to carNumber,
        "carBodyNumber" to carBodyNumber,
        "crashPlace" to crashPlace,
        "crashDetails" to crashDetails,
        "wasAskedOrgans" to wasAskedOrgans,
        "carControl" to carControl,
        "damage" to damage,
        "address" to address
    )

    private fun Map<String, Any>.toInsuranceReport() = InsuranceReport(
        id = this["id"].toString().toInt(),
        number = this["number"].toString().toInt(),
        insuranceId = this["insuranceId"].toString().toInt(),
        insuranceNumber = this["insuranceNumber"].toString().toInt(),
        insuranceSerial = this["insuranceSerial"] as String,
        issuedAt = this["issuedAt"] as Long,
        fullName = this["fullName"] as String,
        phoneNumber = this["phoneNumber"] as String,
        email = this["email"] as String,
        registrationAddress = this["registrationAddress"] as String,
        liveAddress = this["liveAddress"] as String,
        reportRiskType = gson.fromJson(
            this["reportRiskType"] as String,
            ReportRiskType::class.java
        ),
        dateTime = this["dateTime"] as String,
        carNumber = this["carNumber"] as String,
        carBodyNumber = this["carBodyNumber"] as String,
        crashPlace = this["crashPlace"] as String,
        crashDetails = this["crashDetails"] as String,
        wasAskedOrgans = this["wasAskedOrgans"] as String,
        carControl = this["carControl"] as String,
        damage = this["damage"] as String,
        address = this["address"] as String
    )

    private fun Expense.toFirebaseModel() = hashMapOf(
        "username" to LocalRepository.username,
        "id" to id,
        "categoryId" to category.ordinal,
        "sum" to sum,
        "comment" to comment,
        "issuedAt" to issuedAt
    )

    private fun Map<String, Any>.toExpense() = Expense(
        id = this["id"].toString().toInt(),
        category = ExpenseCategory.by(this["categoryId"].toString().toInt()),
        sum = this["sum"].toString().toFloat(),
        comment = this["comment"] as String,
        issuedAt = this["issuedAt"] as Long
    )
}