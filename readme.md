# Błędy:

1. Backend ma źle napisany jedyny test sprawdzający, czy backend działa.
2. CoroutineScope(Dispatchers.Main + job) używane w vm zamiast viewModelScope spowoduje że wyjątek na jednej korutynie spowoduje anulowanie innych korutyn tego scopa.
3. Aplikacja zamknie się z powodu wyjątku, jeśli połączenie z backendem nie powiedzie się.
