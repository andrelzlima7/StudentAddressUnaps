package com.unasp.studantaddress.ui.add

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import com.unasp.studantaddress.api.RetrofitInstance
import com.unasp.studantaddress.models.Student
import com.unasp.studantaddress.repository.StudentRepository
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

data class AddStudentUiState(
    val nome: String = "",
    val ddd: String = "",
    val telefone: String = "",
    val email: String = "",
    val cep: String = "",
    val logradouro: String = "",
    val bairro: String = "",
    val cidade: String = "",
    val uf: String = "",
    val regiao: String = "",
    val isLoadingCep: Boolean = false,
    val isSaving: Boolean = false,
    val cepError: String? = null,
    val saveError: String? = null,
    val saveSuccess: Boolean = false,
    val fieldErrors: Map<String, String> = emptyMap()
)

class AddStudentViewModel(application: Application) : AndroidViewModel(application) {

    private val repository = StudentRepository(application)

    private val _uiState = MutableStateFlow(AddStudentUiState())
    val uiState: StateFlow<AddStudentUiState> = _uiState.asStateFlow()

    fun onNomeChange(value: String) { _uiState.value = _uiState.value.copy(nome = value) }
    fun onDddChange(value: String) { _uiState.value = _uiState.value.copy(ddd = value.take(2)) }
    fun onTelefoneChange(value: String) { _uiState.value = _uiState.value.copy(telefone = value.take(9)) }
    fun onEmailChange(value: String) { _uiState.value = _uiState.value.copy(email = value) }
    fun onLogradouroChange(value: String) { _uiState.value = _uiState.value.copy(logradouro = value) }
    fun onBairroChange(value: String) { _uiState.value = _uiState.value.copy(bairro = value) }
    fun onCidadeChange(value: String) { _uiState.value = _uiState.value.copy(cidade = value) }
    fun onUfChange(value: String) { _uiState.value = _uiState.value.copy(uf = value.take(2).uppercase()) }
    fun onRegiaoChange(value: String) { _uiState.value = _uiState.value.copy(regiao = value) }

    fun onCepChange(value: String) {
        val digits = value.filter { it.isDigit() }.take(8)
        _uiState.value = _uiState.value.copy(cep = digits, cepError = null)
        if (digits.length == 8) buscarCep(digits)
    }

    private fun buscarCep(cep: String) {
        viewModelScope.launch {
            _uiState.value = _uiState.value.copy(isLoadingCep = true, cepError = null)
            try {
                val response = RetrofitInstance.api.buscarCep(cep)
                if (response.isSuccessful) {
                    val body = response.body()
                    if (body != null && body.erro != true) {
                        _uiState.value = _uiState.value.copy(
                            logradouro = body.logradouro,
                            bairro = body.bairro,
                            cidade = body.localidade,
                            uf = body.uf,
                            regiao = body.regiao,
                            ddd = body.ddd,
                            isLoadingCep = false
                        )
                    } else {
                        _uiState.value = _uiState.value.copy(
                            isLoadingCep = false,
                            cepError = "CEP não encontrado"
                        )
                    }
                } else {
                    _uiState.value = _uiState.value.copy(
                        isLoadingCep = false,
                        cepError = "Erro ao buscar CEP"
                    )
                }
            } catch (e: Exception) {
                _uiState.value = _uiState.value.copy(
                    isLoadingCep = false,
                    cepError = "Sem conexão com a internet"
                )
            }
        }
    }

    fun salvar() {
        val state = _uiState.value
        val errors = mutableMapOf<String, String>()

        if (state.nome.isBlank()) errors["nome"] = "Nome é obrigatório"
        if (state.ddd.isBlank()) errors["ddd"] = "DDD é obrigatório"
        if (state.telefone.isBlank()) errors["telefone"] = "Telefone é obrigatório"
        if (state.email.isBlank()) errors["email"] = "E-mail é obrigatório"
        else if (!android.util.Patterns.EMAIL_ADDRESS.matcher(state.email).matches()) errors["email"] = "E-mail inválido"
        if (state.cep.isBlank()) errors["cep"] = "CEP é obrigatório"

        if (errors.isNotEmpty()) {
            _uiState.value = state.copy(fieldErrors = errors)
            return
        }

        viewModelScope.launch {
            _uiState.value = _uiState.value.copy(isSaving = true, saveError = null)
            try {
                val student = Student(
                    nome = state.nome,
                    ddd = state.ddd,
                    telefone = state.telefone,
                    email = state.email,
                    cep = state.cep,
                    logradouro = state.logradouro,
                    bairro = state.bairro,
                    cidade = state.cidade,
                    uf = state.uf,
                    regiao = state.regiao
                )
                repository.save(student)
                _uiState.value = AddStudentUiState(saveSuccess = true)
            } catch (e: Exception) {
                _uiState.value = _uiState.value.copy(
                    isSaving = false,
                    saveError = "Erro ao salvar estudante"
                )
            }
        }
    }
}