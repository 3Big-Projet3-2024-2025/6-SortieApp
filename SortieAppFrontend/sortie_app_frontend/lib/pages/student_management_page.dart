import 'package:flutter/material.dart';
import 'package:http/http.dart' as http;
import 'dart:convert';
import 'package:file_picker/file_picker.dart';
import 'dart:typed_data'; // Import nécessaire pour Uint8List

import '../utils/backendRequest.dart';
import '../utils/router.dart';

class student_management_page extends StatelessWidget {
  const student_management_page({super.key});

  @override
  Widget build(BuildContext context) {
    return Scaffold(
      appBar: AppBar(
        leading: IconButton(
          icon: const Icon(Icons.arrow_back, color: Colors.white),
          onPressed: () {
            Navigator.pop(context); // Retour à la page précédente
          },
        ),
        title: const Text(
          'Sortie\'App',
          style: TextStyle(
            color: Colors.white,
            fontWeight: FontWeight.bold,
          ),
        ),
        backgroundColor: const Color(0xFF0052CC),
      ),
      body: const StudentManagementScreen(),
    );
  }
}

class StudentManagementScreen extends StatefulWidget {
  const StudentManagementScreen({super.key});

  @override
  _StudentManagementScreenState createState() =>
      _StudentManagementScreenState();
}

class _StudentManagementScreenState extends State<StudentManagementScreen> {
  List users = [];
  List filteredUsers = [];
  List roles = [];
  String searchQuery = "";

  /// On stocke ici l'id de l'école de l'utilisateur connecté
  int? _schoolId;

  @override
  void initState() {
    super.initState();
    fetchUsers();
    fetchRoles();
  }

  Future<void> fetchUsers() async {
    try {
      var header = await getHeader();
      var uri = getBackendUrl();

      // Récupération du profil
      final profileResponse = await http.get(
        Uri.parse('$uri/users/profile'),
        headers: header,
      );

      if (profileResponse.statusCode == 200) {
        final userData = json.decode(profileResponse.body);
        final schoolId = userData['school_user']['id_school'];

        // Récupération des étudiants
        final studentsResponse = await http.get(
          Uri.parse('$uri/schools/getStudentsBySchool/$schoolId'),
          headers: header,
        );

        if (studentsResponse.statusCode == 200) {
          // On décode la réponse
          final allUsers = json.decode(studentsResponse.body);

          // On ne garde que ceux qui sont "activated" == true
          final activeUsers = allUsers.where((user) => user['activated'] == true).toList();

          setState(() {
            users = activeUsers;
            filteredUsers = activeUsers;
            _schoolId = schoolId; // Pour l’ajout ultérieur
          });
        } else {
          throw Exception('Failed to load students');
        }
      } else {
        throw Exception('Failed to load user profile');
      }
    } catch (e) {
      print('Error fetching students: $e');
    }
  }

  Future<void> fetchRoles() async {
    try {
      final response = await http.get(Uri.parse('${getBackendUrl()}/roles'));
      if (response.statusCode == 200) {
        setState(() {
          roles = json.decode(response.body);
        });
      } else {
        throw Exception('Failed to load roles');
      }
    } catch (e) {
      print('Error fetching roles: $e');
    }
  }

  /// Met à jour un utilisateur existant
  Future<void> updateUser(
      int id,
      String name_user,
      String lastname_user,
      String email,
      String address_user,
      int id_role,
      bool activated,
      ) async {
    try {
      final response = await http.put(
        Uri.parse('${getBackendUrl()}/users/$id'),
        headers: {'Content-Type': 'application/json'},
        body: json.encode({
          'name_user': name_user,
          'lastname_user': lastname_user,
          'email': email,
          'address_user': address_user,
          'role_user': {'id_role': id_role},
          'activated': activated,
        }),
      );
      if (response.statusCode == 200) {
        fetchUsers();
      }
    } catch (e) {
      print('Error: $e');
    }
  }

  /// Crée un nouvel utilisateur en lui attribuant l'id_school
  Future<void> createUser(
      String name_user,
      String lastname_user,
      String email,
      String address_user,
      int id_role,
      bool activated,
      ) async {
    try {
      final response = await http.post(
        Uri.parse('${getBackendUrl()}/users'),
        headers: {'Content-Type': 'application/json'},
        body: json.encode({
          'name_user': name_user,
          'lastname_user': lastname_user,
          'email': email,
          'address_user': address_user,
          'role_user': {'id_role': id_role},
          'activated': activated,
          // On associe la même école que la personne connectée
          'school_user': {
            'id_school': _schoolId, // <-- c'est ici l'important
          },
        }),
      );
      if (response.statusCode == 200 || response.statusCode == 201) {
        fetchUsers();
      }
    } catch (e) {
      print('Error creating user: $e');
    }
  }

  /// Montre le dialogue pour choisir un fichier CSV
  void showImportCSVDialog(BuildContext context) async {
    FilePickerResult? result = await FilePicker.platform.pickFiles(
      type: FileType.custom,
      allowedExtensions: ['csv'],
    );

    if (result != null) {
      Uint8List? fileBytes = result.files.single.bytes;
      String fileName = result.files.single.name;

      try {
        var request = http.MultipartRequest(
          'POST',
          Uri.parse('${getBackendUrl()}/users/import'),
        );

        if (fileBytes != null) {
          request.files.add(
            http.MultipartFile.fromBytes(
              'file',
              fileBytes,
              filename: fileName,
            ),
          );
        }

        var response = await request.send();

        if (response.statusCode == 200) {
          ScaffoldMessenger.of(context).showSnackBar(
            const SnackBar(
              content: Text('CSV imported successfully!'),
              backgroundColor: Colors.green,
            ),
          );
          fetchUsers(); // Recharge les utilisateurs
        } else {
          ScaffoldMessenger.of(context).showSnackBar(
            const SnackBar(
              content: Text('Failed to import CSV.'),
              backgroundColor: Colors.red,
            ),
          );
        }
      } catch (e) {
        ScaffoldMessenger.of(context).showSnackBar(
          SnackBar(
            content: Text('Error: $e'),
            backgroundColor: Colors.red,
          ),
        );
      }
    } else {
      ScaffoldMessenger.of(context).showSnackBar(
        const SnackBar(
          content: Text('No file selected.'),
          backgroundColor: Colors.orange,
        ),
      );
    }
  }

  /// Supprime un utilisateur après confirmation
  Future<void> deleteUser(int id) async {
    final confirm = await showDialog(
      context: context,
      builder: (context) => AlertDialog(
        title: const Text('Confirm Deletion'),
        content: const Text('Are you sure you want to delete this user?'),
        actions: [
          TextButton(
            onPressed: () => Navigator.pop(context, false),
            child: const Text('Cancel'),
          ),
          TextButton(
            onPressed: () => Navigator.pop(context, true),
            child: const Text('Yes'),
          ),
        ],
      ),
    );

    if (confirm == true) {
      try {
        final response = await http.delete(
          Uri.parse('${getBackendUrl()}/users/$id'),
        );
        if (response.statusCode == 200) {
          fetchUsers();
        }
      } catch (e) {
        print('Error: $e');
      }
    }
  }

  /// Ouvre le dialogue pour modifier un utilisateur
  void showEditUserDialog(
      int id,
      String currentName,
      String currentLastname,
      String currentEmail,
      String currentAddress,
      int currentRoleId,
      bool currentActivated,
      ) async {
    final TextEditingController nameController = TextEditingController();
    final TextEditingController lastnameController = TextEditingController();
    final TextEditingController emailController = TextEditingController();
    final TextEditingController addressController = TextEditingController();

    nameController.text = currentName;
    lastnameController.text = currentLastname;
    emailController.text = currentEmail;
    addressController.text = currentAddress;

    int? selectedRoleId = currentRoleId;
    String? errorMessage;

    showDialog(
      context: context,
      builder: (context) {
        return StatefulBuilder(
          builder: (context, setState) {
            return AlertDialog(
              title: const Text('Edit User'),
              content: SingleChildScrollView(
                child: Column(
                  mainAxisSize: MainAxisSize.min,
                  children: [
                    if (errorMessage != null)
                      Padding(
                        padding: const EdgeInsets.only(bottom: 8.0),
                        child: Text(
                          errorMessage!,
                          style: const TextStyle(color: Colors.red),
                        ),
                      ),
                    TextField(
                      controller: nameController,
                      decoration:
                      const InputDecoration(hintText: 'Enter name'),
                    ),
                    TextField(
                      controller: lastnameController,
                      decoration:
                      const InputDecoration(hintText: 'Enter lastname'),
                    ),
                    TextField(
                      controller: emailController,
                      decoration:
                      const InputDecoration(hintText: 'Enter email'),
                    ),
                    TextField(
                      controller: addressController,
                      decoration:
                      const InputDecoration(hintText: 'Enter address'),
                    ),
                    DropdownButtonFormField<int>(
                      value: selectedRoleId,
                      decoration: const InputDecoration(hintText: 'Select Role'),
                      items: roles.map<DropdownMenuItem<int>>((role) {
                        return DropdownMenuItem<int>(
                          value: role['id_role'],
                          child: Text(role['name_role']),
                        );
                      }).toList(),
                      onChanged: (value) {
                        setState(() {
                          selectedRoleId = value;
                        });
                      },
                    ),
                    TextField(
                      enabled: false,
                      decoration: InputDecoration(
                        labelText: 'Activated',
                        hintText: currentActivated ? 'Yes' : 'No',
                        labelStyle: const TextStyle(color: Colors.grey),
                        border: const OutlineInputBorder(),
                      ),
                    ),
                  ],
                ),
              ),
              actions: [
                TextButton(
                  onPressed: () => Navigator.pop(context),
                  child: const Text('Cancel'),
                ),
                TextButton(
                  onPressed: () {
                    if (nameController.text.isEmpty ||
                        lastnameController.text.isEmpty ||
                        emailController.text.isEmpty ||
                        addressController.text.isEmpty ||
                        selectedRoleId == null) {
                      setState(() {
                        errorMessage = 'All fields must be filled out.';
                      });
                    } else {
                      setState(() {
                        errorMessage = null;
                      });
                      updateUser(
                        id,
                        nameController.text,
                        lastnameController.text,
                        emailController.text,
                        addressController.text,
                        selectedRoleId!,
                        currentActivated,
                      );
                      Navigator.pop(context);
                    }
                  },
                  child: const Text('Update'),
                ),
              ],
            );
          },
        );
      },
    );
  }

  /// Ouvre le dialogue pour ajouter un utilisateur
  void showAddUserDialog() {
    final TextEditingController nameController = TextEditingController();
    final TextEditingController lastnameController = TextEditingController();
    final TextEditingController emailController = TextEditingController();
    final TextEditingController addressController = TextEditingController();

    int? selectedRoleId;
    String? errorMessage;

    showDialog(
      context: context,
      builder: (context) {
        return StatefulBuilder(builder: (context, setState) {
          return AlertDialog(
            title: const Text('Ajouter un utilisateur'),
            content: SingleChildScrollView(
              child: Column(
                mainAxisSize: MainAxisSize.min,
                children: [
                  if (errorMessage != null)
                    Padding(
                      padding: const EdgeInsets.only(bottom: 8.0),
                      child: Text(
                        errorMessage!,
                        style: const TextStyle(color: Colors.red),
                      ),
                    ),
                  TextField(
                    controller: nameController,
                    decoration: const InputDecoration(hintText: 'Nom'),
                  ),
                  TextField(
                    controller: lastnameController,
                    decoration: const InputDecoration(hintText: 'Prénom'),
                  ),
                  TextField(
                    controller: emailController,
                    decoration: const InputDecoration(hintText: 'Email'),
                  ),
                  TextField(
                    controller: addressController,
                    decoration: const InputDecoration(hintText: 'Adresse'),
                  ),
                  const SizedBox(height: 10),
                  DropdownButtonFormField<int>(
                    value: selectedRoleId,
                    decoration: const InputDecoration(hintText: 'Rôle'),
                    items: roles.map<DropdownMenuItem<int>>((role) {
                      return DropdownMenuItem<int>(
                        value: role['id_role'],
                        child: Text(role['name_role']),
                      );
                    }).toList(),
                    onChanged: (value) {
                      setState(() {
                        selectedRoleId = value;
                      });
                    },
                  ),
                ],
              ),
            ),
            actions: [
              TextButton(
                onPressed: () => Navigator.pop(context),
                child: const Text('Annuler'),
              ),
              TextButton(
                onPressed: () {
                  if (nameController.text.isEmpty ||
                      lastnameController.text.isEmpty ||
                      emailController.text.isEmpty ||
                      addressController.text.isEmpty ||
                      selectedRoleId == null) {
                    setState(() {
                      errorMessage = 'Veuillez remplir tous les champs.';
                    });
                  } else {
                    setState(() {
                      errorMessage = null;
                    });
                    // On crée le nouvel utilisateur, en lui attribuant
                    // l'id_school via _schoolId
                    createUser(
                      nameController.text,
                      lastnameController.text,
                      emailController.text,
                      addressController.text,
                      selectedRoleId!,
                      false, // <--- ICI : l'utilisateur sera "activated = false"
                    );
                    Navigator.pop(context);
                  }
                },
                child: const Text('Ajouter'),
              ),
            ],
          );
        });
      },
    );
  }

  @override
  Widget build(BuildContext context) {
    // Ici, on fait un Scaffold interne pour avoir un corps et des FAB
    return Scaffold(

      body: Column(
        children: [
          // Champ de recherche
          Padding(
            padding: const EdgeInsets.all(8.0),
            child: TextField(
              onChanged: (query) {
                setState(() {
                  searchQuery = query.toLowerCase();
                  filteredUsers = users.where((user) {
                    final name = user['name_user']?.toLowerCase() ?? '';
                    final lastname = user['lastname_user']?.toLowerCase() ?? '';
                    final email = user['email']?.toLowerCase() ?? '';
                    return name.contains(searchQuery) ||
                        lastname.contains(searchQuery) ||
                        email.contains(searchQuery);
                  }).toList();
                });
              },
              decoration: InputDecoration(
                hintText: 'Search',
                prefixIcon: const Icon(Icons.search),
                border: OutlineInputBorder(
                  borderRadius: BorderRadius.circular(8.0),
                  borderSide: BorderSide.none,
                ),
                filled: true,
                fillColor: Colors.white,
                contentPadding: const EdgeInsets.symmetric(vertical: 12.0),
              ),
            ),
          ),
          // Liste des utilisateurs
          Expanded(
            child: ListView.builder(
              itemCount: filteredUsers.length,
              itemBuilder: (context, index) {
                final user = filteredUsers[index];
                return ListTile(
                  title: Text('${user['name_user']} '
                      '${user['lastname_user']}'),
                  subtitle: Text(
                    'Role: ${user['role_user']?['name_role'] ?? 'Unknown Role'}',
                  ),
                  trailing: Row(
                    mainAxisSize: MainAxisSize.min,
                    children: [
                      IconButton(
                        icon: const Icon(Icons.edit, color: Colors.blue),
                        onPressed: () => showEditUserDialog(
                          user['id'],
                          user['name_user'] ?? '',
                          user['lastname_user'] ?? '',
                          user['email'] ?? '',
                          user['address_user'] ?? '',
                          user['role_user']?['id_role'] ?? 0,
                          user['activated'] ?? false,
                        ),
                      ),
                      IconButton(
                        icon: const Icon(Icons.delete, color: Colors.red),
                        onPressed: () => deleteUser(user['id']),
                      ),
                    ],
                  ),
                );
              },
            ),
          ),
        ],
      ),
      // Deux boutons en bas à droite
      floatingActionButton: Row(
        mainAxisAlignment: MainAxisAlignment.end,
        children: [
          // Bouton pour importer un CSV
          FloatingActionButton(
            heroTag: 'importCsv',
            backgroundColor: Colors.orange,
            onPressed: () => showImportCSVDialog(context),
            child: const Icon(Icons.upload_file, color: Colors.white),
          ),
          const SizedBox(width: 16),
          // Bouton pour ajouter un utilisateur
          FloatingActionButton(
            heroTag: 'addUser',
            backgroundColor: Color(0xFF0052CC),
            onPressed: showAddUserDialog,
            child: const Icon(Icons.person_add, color: Colors.white),
          ),
        ],
      ),
    );
  }
}
