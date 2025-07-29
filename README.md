# Zauważone błędy:

1. Backend ma źle napisany jedyny test sprawdzający, czy backend działa.
2. CoroutineScope(Dispatchers.Main + job) używane w vm zamiast viewModelScope spowoduje że wyjątek na jednej korutynie spowoduje anulowanie innych korutyn tego scopa.
3. Aplikacja zamknie się z powodu wyjątku, jeśli połączenie z backendem nie powiedzie się - nieodpowiednie obsłużenie błędu.
4. Po dodaniu notatki np. "asd", usunięciu jej i dodaniu kolejnej notatki z tą samą treścią, nie zostanie ona dodana (powód rememberSaveable - Compose przywraca ten sam stan, który był “StartToEnd”, czyli “już usunięto” — więc LaunchedEffect(note.id) się wywołuje i… onDismiss() leci od razu.)
