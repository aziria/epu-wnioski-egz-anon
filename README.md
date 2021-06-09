# epu-wnioski-egz-anon
Zanonimizowana wersja aplikacji pozwalającej na masową wysyłkę wniosków egzekucyjnych w Elektronicznym Postępowaniu Upomiawczym przez API e-sądu napisana dla mojego pracodawcy.
Aplikacja tworzy wnioski na podstawie dostarczonego pliku CSV generowanego wcześniej z bazy danych.

Wykonywalny JAR tworzony jest za pomocą polecenia ```mvn clean compile assembly:single```. W folderze z programem nalezy umieścić plik o nazwie ```firma.csv```, a informacje o walidacji wniosków egzekucyjnych zapisywane są do pliku ```odpowiedz.txt```.
