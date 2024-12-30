import 'package:flutter/material.dart';
import 'package:flutter/foundation.dart'; // For kIsWeb
import 'package:http/http.dart' as http;
import 'dart:convert';
import 'package:get/get.dart';

import '../utils/router.dart';

class SchoolsCrud extends StatelessWidget {
  const SchoolsCrud({super.key});

  @override
  Widget build(BuildContext context) {
    return Scaffold(
      appBar: AppBar(
        title: const Text('Schools Management'),
        backgroundColor: const Color(0xFF87CEEB),
        actions: [
          IconButton(
            icon: const Icon(Icons.logout),
            onPressed: () async {
              redirectHome();
            },
          ),
        ],
      ),
      body: const SchoolListScreen(),
    );
  }
}

class SchoolListScreen extends StatefulWidget {
  const SchoolListScreen({super.key});

  @override
  _SchoolListScreenState createState() => _SchoolListScreenState();
}

class _SchoolListScreenState extends State<SchoolListScreen> {
  String getBackendUrl() {
    if (kIsWeb) {
      return 'http://localhost:8081'; // URL Backend for Web
    } else {
      return 'http://10.0.2.2:8081'; // URL Backend for Android Emulator
    }
  }

  late String apiUrl;

  List schools = [];

  List filteredSchools = [];  // Liste filtrée des écoles
  final TextEditingController searchController = TextEditingController();



  @override
  void initState() {
    super.initState();
    apiUrl = '${getBackendUrl()}/schools';
    fetchSchools();

  // Écoute les changements dans la barre de recherche
  searchController.addListener(() {
  filterSchools();
  });
}

  Future<void> fetchSchools() async {
    try {
      final response = await http.get(Uri.parse(apiUrl));
      if (response.statusCode == 200) {
        setState(() {
          schools = json.decode(response.body);
          filteredSchools = schools;  // Initialiser la liste filtrée
        });
      } else {
        throw Exception('Failed to load schools');
      }
    } catch (e) {
      print('Error: $e');
    }
  }

  // Fonction pour filtrer les écoles
  void filterSchools() {
    String query = searchController.text.toLowerCase();
    setState(() {
      filteredSchools = schools.where((school) {
        return school['name_school'].toLowerCase().contains(query);
      }).toList();
    });
  }



  Future<void> addSchool(String nameSchool, String addressSchool) async {
    try {
      final response = await http.post(
        Uri.parse(apiUrl),
        headers: {'Content-Type': 'application/json'},
        body: json.encode({
          'name_school': nameSchool,
          'address_school': addressSchool,
        }),
      );
      if (response.statusCode == 200) {
        fetchSchools();
      }
    } catch (e) {
      print('Error: $e');
    }
  }

  Future<void> updateSchool(
      int idSchool, String nameSchool, String addressSchool) async {
    try {
      final response = await http.put(
        Uri.parse('$apiUrl/$idSchool'),
        headers: {'Content-Type': 'application/json'},
        body: json.encode({
          'name_school': nameSchool,
          'address_school': addressSchool,
        }),
      );
      if (response.statusCode == 200) {
        fetchSchools();
      }
    } catch (e) {
      print('Error: $e');
    }
  }

  Future<void> deleteSchool(int idSchool) async {
    // Afficher un dialog de confirmation avant de supprimer
    bool? confirmDelete = await showDialog<bool>(
      context: context,
      builder: (context) {
        return AlertDialog(
          title: const Text('Confirm Deletion'),
          content: const Text('Are you sure you want to delete this school?'),
          actions: [
            TextButton(
              onPressed: () {
                Navigator.pop(context, false);  // Renvoyer false (annulation)
              },
              child: const Text('No'),
            ),
            TextButton(
              onPressed: () {
                Navigator.pop(context, true);  // Renvoyer true (confirmation)
              },
              child: const Text('Yes'),
            ),
          ],
        );
      },
    );

    // Si la suppression est confirmée, faire la requête DELETE
    if (confirmDelete == true) {
      try {
        final response = await http.delete(Uri.parse('$apiUrl/$idSchool'));
        if (response.statusCode == 200) {
          fetchSchools();  // Rafraîchir la liste après suppression
          Get.snackbar('Success', 'School deleted successfully');
        } else {
          Get.snackbar('Error', 'Failed to delete school');
        }
      } catch (e) {
        print('Error: $e');
        Get.snackbar('Error', 'An error occurred while deleting');
      }
    } else {
      // Afficher un message ou simplement ne rien faire
      print('Deletion cancelled');
    }
  }


  void showAddSchoolDialog() async {
    final TextEditingController nameController = TextEditingController();
    final TextEditingController addressController = TextEditingController();

    showDialog(
      context: context,
      builder: (context) {
        return AlertDialog(
          title: const Text('Add School'),
          content: Column(
            mainAxisSize: MainAxisSize.min,
            children: [
              TextField(
                controller: nameController,
                decoration: const InputDecoration(
                    hintText: 'Enter school name'),
              ),
              TextField(
                controller: addressController,
                decoration: const InputDecoration(
                    hintText: 'Enter school address'),
              ),
            ],
          ),
          actions: [
            TextButton(
              onPressed: () => Navigator.pop(context),
              child: const Text('Cancel'),
            ),
            TextButton(
              onPressed: () {
                // Validation des champs avant l'ajout
                if (nameController.text.isEmpty ||
                    addressController.text.isEmpty) {
                  // Afficher un message d'erreur si les champs sont vides
                  Get.snackbar(
                    'Validation Error',
                    'All fields must be filled.',
                    snackPosition: SnackPosition.BOTTOM,
                    backgroundColor: Colors.redAccent,
                    colorText: Colors.white,
                  );
                  return;  // Ne pas fermer la boîte de dialogue tant que ce n'est pas valide
                }

                // Si tout est bon, ajouter l'école
                addSchool(
                  nameController.text,
                  addressController.text,
                );
                Navigator.pop(context);
              },
              child: const Text('Add'),
            ),
          ],
        );
      },
    );
  }

  void showEditSchoolDialog(
      int idSchool, String currentName, String currentAddress) async {
    final TextEditingController nameController = TextEditingController();
    final TextEditingController addressController = TextEditingController();

    nameController.text = currentName;
    addressController.text = currentAddress;

    showDialog(
      context: context,
      builder: (context) {
        return AlertDialog(
          title: const Text('Edit School'),
          content: Column(
            mainAxisSize: MainAxisSize.min,
            children: [
              TextField(
                controller: nameController,
                decoration:
                const InputDecoration(hintText: 'Enter school name'),
              ),
              TextField(
                controller: addressController,
                decoration:
                const InputDecoration(hintText: 'Enter school address'),
              ),
            ],
          ),
          actions: [
            TextButton(
              onPressed: () => Navigator.pop(context),
              child: const Text('Cancel'),
            ),
            TextButton(
              onPressed: () {
                // Validation des champs avant la mise à jour
                if (nameController.text.isEmpty ||
                    addressController.text.isEmpty) {
                  // Afficher un message d'erreur si les champs sont vides
                  Get.snackbar(
                    'Validation Error',
                    'All fields must be filled.',
                    snackPosition: SnackPosition.BOTTOM,
                    backgroundColor: Colors.redAccent,
                    colorText: Colors.white,
                  );
                  return;
                }

                // Si tout est bon, procéder à la mise à jour
                updateSchool(
                  idSchool,
                  nameController.text,
                  addressController.text,
                );
                Navigator.pop(context);
              },
              child: const Text('Update'),
            ),
          ],
        );
      },
    );
  }

  void showDeleteConfirmationDialog(int idSchool) {
    showDialog(
      context: context,
      builder: (context) {
        return AlertDialog(
          title: const Text('Confirm Deletion'),
          content: const Text('Are you sure you want to delete this school?'),
          actions: [
            TextButton(
              onPressed: () {
                Navigator.pop(context);  // Fermer le dialog sans rien faire
              },
              child: const Text('No'),
            ),
            TextButton(
              onPressed: () {
                deleteSchool(idSchool);  // Supprime l'école si on clique sur "Yes"
                Navigator.pop(context);  // Fermer le dialog
              },
              child: const Text('Yes'),
            ),
          ],
        );
      },
    );
  }

  void showSchoolDetailsDialog(int idSchool, List students) {
    showDialog(
      context: context,
      builder: (context) {
        return AlertDialog(
          title: Text('School Details'),
          content: Container(
            width: double.maxFinite,  // Permet à la boîte de prendre la largeur maximale
            constraints: const BoxConstraints(
              maxHeight: 400,  // Hauteur maximale du Dialog
            ),
            child: students.isNotEmpty
                ? ListView.builder(
              shrinkWrap: true,  // Empêche ListView de prendre plus de place qu'il n'en a besoin
              itemCount: students.length,
              itemBuilder: (context, index) {
                final student = students[index];
                return ListTile(
                  leading: Icon(
                    student['activated'] == true
                        ? Icons.check_circle
                        : Icons.cancel,
                    color: student['activated'] == true
                        ? Colors.green
                        : Colors.red,
                  ),
                  title: Text(student['name_user'] ?? 'Unnamed'),
                  subtitle: Text('Email: ${student['email'] ?? 'N/A'}'),
                );
              },
            )
                : const Text('No students found for this school.'),
          ),
          actions: [
            TextButton(
              onPressed: () => Navigator.pop(context),
              child: const Text('Close'),
            ),
          ],
        );
      },
    );
  }


  Future<void> fetchSchoolDetails(int idSchool) async {
    try {
      final response = await http.get(Uri.parse('$apiUrl/getUsersBySchool/$idSchool'));

      print('Response: ${response.body}');  // 🔍 Log des données reçues

      if (response.statusCode == 200) {
        List students = json.decode(response.body);
        print('Students: $students');  // 🔍 Log de la liste d'étudiants
        showSchoolDetailsDialog(idSchool, students);
      } else {
        Get.snackbar('Error', 'Failed to load school details');
      }
    } catch (e) {
      print('Error: $e');
      Get.snackbar('Error', 'An error occurred while fetching school details');
    }
  }



  @override
  Widget build(BuildContext context) {
    return Scaffold(
      body: Column(
        children: [
          Padding(
            padding: const EdgeInsets.all(8.0),
            child: TextField(
              controller: searchController,
              decoration: const InputDecoration(
                hintText: 'Search',
                prefixIcon: Icon(Icons.search),
                border: OutlineInputBorder(),
              ),
            ),
          ),
          Expanded(
            child: ListView.builder(
              itemCount: filteredSchools.length,
              itemBuilder: (context, index) {
                final school = filteredSchools[index];
                return ListTile(
                  title: Text(school['name_school']),
                  subtitle: Text(school['address_school']),
                );
              },
            ),
          ),
        ],
      ),
      floatingActionButton: FloatingActionButton(
        onPressed: showAddSchoolDialog,
        child: const Icon(Icons.add),
      ),
    );
  }
}