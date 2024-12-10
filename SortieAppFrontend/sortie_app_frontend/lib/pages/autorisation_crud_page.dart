import 'package:flutter/material.dart';
import 'package:get/get.dart';
import 'package:flutter_secure_storage/flutter_secure_storage.dart';
import 'dart:convert';
import 'package:http/http.dart' as http;


class AutorisationCrudPage extends StatefulWidget {
  const AutorisationCrudPage({super.key});

  @override
  _AutorisationCrudPage createState() => _AutorisationCrudPage();
}

class _AutorisationCrudPage extends State<AutorisationCrudPage>{
  List _Autorisations = [];
  bool _isLoading = true;
  String apiUrl = 'http://localhost:8081/Autorisations';
  Future<void> _fetchAutorisations() async {
    //const apiUrl = 'http://localhost:8081/Autorisations';

    try {
      final response = await http.get(Uri.parse(apiUrl));
      final data = json.decode(response.body);

      if (response.statusCode == 200) {
        setState(() {
          _Autorisations = data['data'] ?? [];
          _isLoading = false;
        });
      } else {
        throw('Erreur lors de la requête');
      }
    }catch(exception){
      print(exception);
      setState(() {
        _isLoading = true;
      });
    }
  }

  Future<void> addAutorisation(String type, String note,
      String date_debut, String date_fin, String heure_debut, String heure_fin, String jours, int userId) async {
    try {
      final response = await http.post(
        Uri.parse(apiUrl),
        headers: {'Content-Type': 'application/json'},
        body: json.encode({
          'type': type,
          'note': note,
          'date_debut': date_debut,
          'date_fin': date_fin,
          'heure_debut': heure_debut,
          'heure_fin': heure_fin,
          'jours': jours,
          'user': {'id': userId}, // Send role ID
        }),
      );
      if (response.statusCode == 200) {
        _fetchAutorisations(); // Refresh the user list after adding
      }
    } catch (e) {
      print('Error: $e');
    }
  }

  Future<void> updateAutorisation(int id, String type, String note,
      String date_debut, String date_fin, String heure_debut, String heure_fin, String jours, int userId) async {
    try {
      final response = await http.put(
        Uri.parse('$apiUrl/$id'),
        headers: {'Content-Type': 'application/json'},
        body: json.encode({
          'id': id,
          'type': type,
          'note': note,
          'date_debut': date_debut,
          'date_fin': date_fin,
          'heure_debut': heure_debut,
          'heure_fin': heure_fin,
          'jours': jours,
          'user': {'id': userId}, // Send role ID
        }),
      );
      if (response.statusCode == 200) {
        _fetchAutorisations(); // Refresh the user list after adding
      }
    } catch (e) {
      print('Error: $e');
    }
  }

  Future<void> deleteAutorisation(int id) async {
    try {
      final response = await http.delete(Uri.parse('$apiUrl/$id'));
      if (response.statusCode == 200) {
        _fetchAutorisations(); // Refresh the user list after deleting
      }
    } catch (e) {
      print('Error: $e');
    }
  }

  @override
  void initState() {
    super.initState();
    _fetchAutorisations();
  }
  @override
  Widget build(BuildContext context){
    return Scaffold(
    );
  }
}