package com.unasp.studantaddress.repository

import android.content.Context
import android.net.ConnectivityManager
import android.net.NetworkCapabilities
import com.unasp.studantaddress.api.StudentRetrofitInstance
import com.unasp.studantaddress.local.AppDatabase
import com.unasp.studantaddress.local.StudentEntity
import com.unasp.studantaddress.models.Student
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map

class StudentRepository(context: Context) {

    private val dao = AppDatabase.getInstance(context).studentDao()
    private val api = StudentRetrofitInstance.api
    private val connectivity = context.getSystemService(Context.CONNECTIVITY_SERVICE)
            as ConnectivityManager

    // Verifica conexão
    private fun isOnline(): Boolean {
        val network = connectivity.activeNetwork ?: return false
        val caps = connectivity.getNetworkCapabilities(network) ?: return false
        return caps.hasCapability(NetworkCapabilities.NET_CAPABILITY_INTERNET)
    }

    // Observa lista local
    fun findAll(): Flow<List<Student>> {
        return dao.findAll().map { list ->
            list.map { it.toStudent() }
        }
    }

    // Salva local e tenta sincronizar
    suspend fun save(student: Student): Long {
        val entity = student.toEntity()
        val localId = dao.insert(entity)

        if (isOnline()) {
            syncPending()
        }

        return localId
    }

    // Remove local e na API se online
    suspend fun delete(localId: Long) {
        val entity = dao.findById(localId) ?: return
        dao.deleteById(localId)

        if (isOnline() && entity.remoteId != null) {
            try {
                api.delete(entity.remoteId)
            } catch (e: Exception) {
                // ignora erro de rede no delete
            }
        }
    }

    // Sincroniza pendentes com a API
    suspend fun syncPending() {
        val pending = dao.findPending()
        pending.forEach { entity ->
            try {
                val response = api.create(entity.toStudent())
                if (response.isSuccessful) {
                    val remoteId = response.body()?.id
                    if (remoteId != null) {
                        dao.markAsSynced(entity.localId, remoteId)
                    }
                }
            } catch (e: Exception) {
            }
        }

        // Após sincronizar, busca lista atualizada da API e salva local
        if (pending.isNotEmpty()) {
            refreshFromApi()
        }
    }

    suspend fun refreshFromApi() {
        if (!isOnline()) return
        try {
            val response = api.findAll()
            if (response.isSuccessful) {
                val remoteStudents = response.body() ?: return
                remoteStudents.forEach { student ->
                    val remoteId = student.id ?: return@forEach

                    // verifica se já existe localmente pelo remoteId
                    val existing = dao.findByRemoteId(remoteId)

                    if (existing != null) {
                        // atualiza o registro existente
                        dao.update(existing.copy(
                            nome = student.nome,
                            ddd = student.ddd,
                            telefone = student.telefone,
                            email = student.email,
                            cep = student.cep,
                            logradouro = student.logradouro,
                            bairro = student.bairro,
                            cidade = student.cidade,
                            uf = student.uf,
                            regiao = student.regiao,
                            synced = true
                        ))
                    } else {
                        // insere apenas se não existir
                        dao.insert(student.toEntity().copy(synced = true))
                    }
                }
            }
        } catch (e: Exception) {
        }
    }

    // Mappers
    private fun StudentEntity.toStudent() = Student(
        localId = localId,
        id = remoteId,
        nome = nome,
        ddd = ddd,
        telefone = telefone,
        email = email,
        cep = cep,
        logradouro = logradouro,
        bairro = bairro,
        cidade = cidade,
        uf = uf,
        regiao = regiao
    )

    private fun Student.toEntity() = StudentEntity(
        remoteId = id,
        nome = nome,
        ddd = ddd,
        telefone = telefone,
        email = email,
        cep = cep,
        logradouro = logradouro,
        bairro = bairro,
        cidade = cidade,
        uf = uf,
        regiao = regiao,
        synced = false
    )
}