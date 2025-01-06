import 'package:flutter/material.dart';
import 'package:http/http.dart' as http;
import 'dart:convert';
import 'package:file_picker/file_picker.dart';
import 'dart:typed_data'; // For Uint8List

import '../utils/backendRequest.dart';
import '../utils/router.dart';

class Supervisors_management_page extends StatelessWidget {
  const Supervisors_management_page({super.key});

  @override
  Widget build(BuildContext context) {
    return Scaffold(
      appBar: AppBar(
        leading: IconButton(
          icon: const Icon(Icons.arrow_back, color: Colors.white),
          onPressed: () {
            // Go back to the previous page
            Navigator.pop(context);
          },
        ),
        title: const Text(
          'Sortie\'App',
          style: TextStyle(
            color: Colors.white,
            fontWeight: FontWeight.bold,
          ),
        ),
        backgroundColor: const Color(0xFF0052CC), // Navy blue
      ),
      body: const SupervisorsManagementScreen(),
    );
  }
}

class SupervisorsManagementScreen extends StatefulWidget {
  const SupervisorsManagementScreen({super.key});

  @override
  _SupervisorsManagementScreenState createState() =>
      _SupervisorsManagementScreenState();
}

class _SupervisorsManagementScreenState
    extends State<SupervisorsManagementScreen> {
  // Will store all fetched supervisors (only those with activated == true)
  List users = [];
  // Will store the filtered list based on the search query
  List filteredUsers = [];
  // Will store all roles fetched from the backend
  List roles = [];
  // Will store the 'id_role' for "Supervisor"
  int? supervisorRoleId;

  // Used to store the user input from the search bar
  String searchQuery = "";
  // School ID of the connected user
  int? _schoolId;

  @override
  void initState() {
    super.initState();
    fetchUsers();
    fetchRoles(); // Fetch roles from the backend to identify "Supervisor"
  }

  /// Fetch all roles from the backend and look for the role "Supervisor".
  Future<void> fetchRoles() async {
    try {
      final header = await getHeader();
      final uri = getBackendUrl();
      final response = await http.get(
        Uri.parse('$uri/roles'),
        headers: header,
      );

      if (response.statusCode == 200) {
        final data = json.decode(response.body);
        setState(() {
          roles = data; // Example: [{id_role:1, name_role:"Admin"}, ...]
        });

        // If you already know the id_role for "Supervisor" (e.g. 3),
        // you can directly do: supervisorRoleId = 3;
        // Otherwise, look for it by name:
        final supervisor = roles.firstWhere(
              (r) => r['name_role'] == 'Supervisor',
          orElse: () => null,
        );
        if (supervisor != null) {
          supervisorRoleId = supervisor['id_role'];
        }
      } else {
        print('Error fetching roles. Status: ${response.statusCode}');
      }
    } catch (e) {
      print('Error fetching roles: $e');
    }
  }

  /// Fetch all supervisors for the connected user's school and keep only those
  /// whose 'activated' property is true.
  Future<void> fetchUsers() async {
    try {
      final header = await getHeader();
      final uri = getBackendUrl();

      // Get the user profile to find the school ID
      final profileResponse = await http.get(
        Uri.parse('$uri/users/profile'),
        headers: header,
      );

      if (profileResponse.statusCode == 200) {
        final userData = json.decode(profileResponse.body);
        final schoolId = userData['school_user']['id_school'];

        // Fetch supervisors
        final supervisorsResponse = await http.get(
          Uri.parse('$uri/schools/getSupervisorBySchool/$schoolId'),
          headers: header,
        );

        if (supervisorsResponse.statusCode == 200) {
          final allSupervisors = json.decode(supervisorsResponse.body);

          // Keep only supervisors with "activated == true"
          final activeSupervisors = allSupervisors
              .where((user) => user['activated'] == true)
              .toList();

          setState(() {
            users = activeSupervisors;
            filteredUsers = activeSupervisors;
            _schoolId = schoolId;
          });
        } else {
          throw Exception('Failed to load supervisors');
        }
      } else {
        throw Exception('Failed to load user profile');
      }
    } catch (e) {
      print('Error fetching supervisors: $e');
    }
  }

  /// Create a new supervisor with the role "Supervisor" (supervisorRoleId) and activated = false.
  Future<void> createUser(
      String nameUser,
      String lastnameUser,
      String email,
      String addressUser,
      int? roleId,
      ) async {
    try {
      final header = await getHeader();
      final response = await http.post(
        Uri.parse('${getBackendUrl()}/users'),
        headers: header,
        body: json.encode({
          'name_user': nameUser,
          'lastname_user': lastnameUser,
          'email': email,
          'address_user': addressUser,
          'activated': false,
          'school_user': {
            'id_school': _schoolId,
          },
          'role_user': {
            'id_role': roleId,
          },
        }),
      );
      if (response.statusCode == 200 || response.statusCode == 201) {
        fetchUsers();
      } else {
        print('Error creating supervisor (HTTP ${response.statusCode}): ${response.body}');
      }
    } catch (e) {
      print('Error creating supervisor: $e');
    }
  }

  /// Update an existing supervisor. We display a dropdown for the role, but it is
  /// disabled, so the user can't change the role in the UI.
  Future<void> updateUser(
      int id,
      String nameUser,
      String lastnameUser,
      String email,
      String addressUser,
      bool activated,
      int? roleId,
      ) async {
    try {
      final header = await getHeader();
      final response = await http.put(
        Uri.parse('${getBackendUrl()}/users/$id'),
        headers: header,
        body: json.encode({
          'name_user': nameUser,
          'lastname_user': lastnameUser,
          'email': email,
          'address_user': addressUser,
          'activated': activated,
          'role_user': {
            'id_role': roleId,
          },
        }),
      );
      if (response.statusCode == 200) {
        fetchUsers();
      } else {
        print('Error updating supervisor (HTTP ${response.statusCode}).');
      }
    } catch (e) {
      print('Error updating supervisor: $e');
    }
  }

  /// Delete a supervisor after showing a confirmation dialog
  Future<void> deleteUser(int id) async {
    final confirm = await showDialog(
      context: context,
      builder: (context) => AlertDialog(
        title: const Text('Confirm Deletion'),
        content: const Text('Are you sure you want to delete this supervisor?'),
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
        final header = await getHeader();
        final response = await http.delete(
          Uri.parse('${getBackendUrl()}/users/$id'),
          headers: header,
        );
        if (response.statusCode == 200) {
          fetchUsers();
        }
      } catch (e) {
        print('Error deleting supervisor: $e');
      }
    }
  }

  /// Show a file picker to import a CSV, adding the auth header before sending
  /// the multipart request to '/users/import'.
  void showImportCSVDialog(BuildContext context) async {
    FilePickerResult? result = await FilePicker.platform.pickFiles(
      type: FileType.custom,
      allowedExtensions: ['csv'],
    );

    if (result != null) {
      Uint8List? fileBytes = result.files.single.bytes;
      String fileName = result.files.single.name;

      try {
        final header = await getHeader();

        var request = http.MultipartRequest(
          'POST',
          Uri.parse('${getBackendUrl()}/users/import'),
        );
        request.headers.addAll(header);

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
          fetchUsers();
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

  /// Open a dialog to add a new supervisor (role set to "Supervisor" by default).
  void showAddUserDialog() {
    final TextEditingController nameController = TextEditingController();
    final TextEditingController lastnameController = TextEditingController();
    final TextEditingController emailController = TextEditingController();
    final TextEditingController addressController = TextEditingController();

    String? errorMessage;
    // Default role = "Supervisor"
    int? selectedRoleId = supervisorRoleId;

    showDialog(
      context: context,
      builder: (context) {
        return StatefulBuilder(builder: (context, setState) {
          return AlertDialog(
            title: const Text('Ajouter un superviseur'),
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
                  const SizedBox(height: 12),
                  // List all roles but disable selection
                  DropdownButtonFormField<int>(
                    value: selectedRoleId,
                    decoration: const InputDecoration(labelText: 'Rôle'),
                    items: roles.map<DropdownMenuItem<int>>((role) {
                      return DropdownMenuItem<int>(
                        value: role['id_role'],
                        child: Text(role['name_role']),
                      );
                    }).toList(),
                    onChanged: null, // Disabled dropdown
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
                      addressController.text.isEmpty) {
                    setState(() {
                      errorMessage = 'Veuillez remplir tous les champs.';
                    });
                  } else {
                    setState(() {
                      errorMessage = null;
                    });
                    createUser(
                      nameController.text,
                      lastnameController.text,
                      emailController.text,
                      addressController.text,
                      selectedRoleId,
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

  /// Open a dialog to edit the existing supervisor. The role dropdown is displayed
  /// but disabled, so the user cannot change the role in the UI.
  void showEditUserDialog(
      int id,
      String currentName,
      String currentLastname,
      String currentEmail,
      String currentAddress,
      bool currentActivated,
      Map? existingRole,
      ) {
    final TextEditingController nameController = TextEditingController();
    final TextEditingController lastnameController = TextEditingController();
    final TextEditingController emailController = TextEditingController();
    final TextEditingController addressController = TextEditingController();

    nameController.text = currentName;
    lastnameController.text = currentLastname;
    emailController.text = currentEmail;
    addressController.text = currentAddress;

    String? errorMessage;
    // Default to the existing role, otherwise Supervisor
    int? selectedRoleId = existingRole?['id_role'] ?? supervisorRoleId;

    showDialog(
      context: context,
      builder: (context) {
        return StatefulBuilder(
          builder: (context, setState) {
            return AlertDialog(
              title: const Text('Modifier le superviseur'),
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
                    const SizedBox(height: 12),
                    DropdownButtonFormField<int>(
                      value: selectedRoleId,
                      decoration: const InputDecoration(labelText: 'Rôle'),
                      items: roles.map<DropdownMenuItem<int>>((role) {
                        return DropdownMenuItem<int>(
                          value: role['id_role'],
                          child: Text(role['name_role']),
                        );
                      }).toList(),
                      onChanged: null, // Disabled
                    ),
                    const SizedBox(height: 12),
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
                  child: const Text('Annuler'),
                ),
                TextButton(
                  onPressed: () {
                    if (nameController.text.isEmpty ||
                        lastnameController.text.isEmpty ||
                        emailController.text.isEmpty ||
                        addressController.text.isEmpty) {
                      setState(() {
                        errorMessage = 'Veuillez remplir tous les champs.';
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
                        currentActivated,
                        selectedRoleId,
                      );
                      Navigator.pop(context);
                    }
                  },
                  child: const Text('Mettre à jour'),
                ),
              ],
            );
          },
        );
      },
    );
  }

  @override
  Widget build(BuildContext context) {
    return Scaffold(
      // No AppBar here because it's defined in the parent widget
      body: Column(
        children: [
          // Search bar
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
          // List of supervisors
          Expanded(
            child: ListView.builder(
              itemCount: filteredUsers.length,
              itemBuilder: (context, index) {
                final user = filteredUsers[index];
                // Retrieve the role name or default to 'Unknown Role'
                final roleName = user['role_user']?['name_role'] ?? 'Unknown Role';

                // Retrieve base64-encoded image if available
                final String? base64Image = user['picture_user'];
                // Decode base64 into Uint8List, or keep null if no valid picture
                Uint8List? imageBytes =
                (base64Image != null && base64Image.trim().isNotEmpty)
                    ? base64Decode(base64Image)
                    : null;

                return ListTile(
                  // Tapping on the image to display a larger version in a dialog
                  leading: GestureDetector(
                    onTap: () {
                      showDialog(
                        context: context,
                        builder: (BuildContext context) {
                          return Dialog(
                            child: Column(
                              mainAxisSize: MainAxisSize.min,
                              children: [
                                imageBytes != null
                                    ? Image.memory(
                                  imageBytes,
                                  fit: BoxFit.cover,
                                )
                                    : Image.asset(
                                  'assets/images/default_profile.jpg',
                                  fit: BoxFit.cover,
                                ),
                                TextButton(
                                  onPressed: () {
                                    Navigator.of(context).pop();
                                  },
                                  child: const Text('Close'),
                                ),
                              ],
                            ),
                          );
                        },
                      );
                    },
                    child: imageBytes != null
                        ? Image.memory(
                      imageBytes,
                      width: 50,
                      height: 50,
                      fit: BoxFit.cover,
                    )
                        : Image.asset(
                      'assets/images/default_profile.jpg',
                      width: 50,
                      height: 50,
                      fit: BoxFit.cover,
                    ),
                  ),
                  title: Text('${user['name_user']} ${user['lastname_user']}'),
                  subtitle: Text('Role: $roleName'),
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
                          user['activated'] ?? false,
                          user['role_user'], // Keep the existing role
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
      // Two FABs at the bottom-right
      floatingActionButton: Row(
        mainAxisAlignment: MainAxisAlignment.end,
        children: [
          // Button to import CSV
          FloatingActionButton(
            heroTag: 'importCsv',
            backgroundColor: Colors.orange,
            onPressed: () => showImportCSVDialog(context),
            child: const Icon(Icons.upload_file, color: Colors.white),
          ),
          const SizedBox(width: 16),
          // Button to add a new supervisor
          FloatingActionButton(
            heroTag: 'addUser',
            backgroundColor: const Color(0xFF0052CC),
            onPressed: showAddUserDialog,
            child: const Icon(Icons.person_add, color: Colors.white),
          ),
        ],
      ),
    );
  }
}
