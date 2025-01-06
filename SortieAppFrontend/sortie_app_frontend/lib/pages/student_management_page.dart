import 'package:flutter/material.dart';
import 'package:http/http.dart' as http;
import 'dart:convert';
import 'package:file_picker/file_picker.dart';
import 'dart:typed_data'; // Necessary for Uint8List

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
  // Will store active students
  List users = [];
  // Will store the filtered list based on the search query
  List filteredUsers = [];
  // Will store all roles fetched from the DB
  List roles = [];
  // Will store the 'id_role' for "Student"
  int? studentRoleId;
  // School ID of the connected user
  int? _schoolId;

  // Used to store the user input from the search bar
  String searchQuery = "";

  @override
  void initState() {
    super.initState();
    fetchUsers();
    fetchRoles(); // Fetch roles to identify "Student"
  }

  /// Fetch all roles from the backend, look for the "Student" role, and store its ID.
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

        final student = roles.firstWhere(
              (r) => r['name_role'] == 'Student',
          orElse: () => null,
        );
        if (student != null) {
          studentRoleId = student['id_role'];
        }
      } else {
        print('Error fetching roles. Status code: ${response.statusCode}');
      }
    } catch (e) {
      print('Error fetching roles: $e');
    }
  }

  /// Fetch students belonging to the logged-in user's school
  /// and keep only those where 'activated' is true.
  Future<void> fetchUsers() async {
    try {
      final header = await getHeader();
      final uri = getBackendUrl();

      // Retrieve the user's profile to get the school ID
      final profileResponse = await http.get(
        Uri.parse('$uri/users/profile'),
        headers: header,
      );

      if (profileResponse.statusCode == 200) {
        final userData = json.decode(profileResponse.body);
        final schoolId = userData['school_user']['id_school'];

        // Fetch the students for that particular school
        final studentsResponse = await http.get(
          Uri.parse('$uri/schools/getStudentsBySchool/$schoolId'),
          headers: header,
        );

        if (studentsResponse.statusCode == 200) {
          final allUsers = json.decode(studentsResponse.body);

          // Keep only users with 'activated == true'
          final activeUsers = allUsers
              .where((user) => user['activated'] == true)
              .toList();

          setState(() {
            users = activeUsers;
            filteredUsers = activeUsers;
            _schoolId = schoolId;
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

  /// Create a new user with the default "Student" role (studentRoleId).
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
          'activated': false, // new user not activated by default
          'school_user': {
            'id_school': _schoolId,
          },
          'role_user': {
            'id_role': roleId, // "Student" role by default
          },
        }),
      );
      if (response.statusCode == 200 || response.statusCode == 201) {
        fetchUsers();
      } else {
        print('Error creating user (HTTP ${response.statusCode}): ${response.body}');
      }
    } catch (e) {
      print('Error creating user: $e');
    }
  }

  /// Update an existing user. The role remains the same (Student), but
  /// we could also keep a disabled dropdown if we wanted to show it in the UI.
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
      }
    } catch (e) {
      print('Error updating user: $e');
    }
  }

  /// Import a CSV file with the correct headers for authentication.
  /// Now calls the "importUsersForAdmin" endpoint instead of "import".
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
          // CHANGEMENT ICI -> /importUsersForAdmin
          Uri.parse('${getBackendUrl()}/users/importUsersForAdmin'),
        );

        // Add headers (e.g., Authorization)
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
      // No file selected
      ScaffoldMessenger.of(context).showSnackBar(
        const SnackBar(
          content: Text('No file selected.'),
          backgroundColor: Colors.orange,
        ),
      );
    }
  }

  /// Delete a user after confirmation
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
        final header = await getHeader();
        final response = await http.delete(
          Uri.parse('${getBackendUrl()}/users/$id'),
          headers: header,
        );
        if (response.statusCode == 200) {
          fetchUsers();
        }
      } catch (e) {
        print('Error deleting user: $e');
      }
    }
  }

  /// Show a dialog to edit an existing user,
  /// with a disabled dropdown for the "Student" role.
  void showEditUserDialog(
      int id,
      String currentName,
      String currentLastname,
      String currentEmail,
      String currentAddress,
      bool currentActivated,
      Map? existingRole,
      ) {
    int? selectedRoleId = existingRole?['id_role'] ?? studentRoleId;

    final TextEditingController nameController = TextEditingController();
    final TextEditingController lastnameController = TextEditingController();
    final TextEditingController emailController = TextEditingController();
    final TextEditingController addressController = TextEditingController();

    nameController.text = currentName;
    lastnameController.text = currentLastname;
    emailController.text = currentEmail;
    addressController.text = currentAddress;

    String? errorMessage;

    showDialog(
      context: context,
      builder: (context) {
        return StatefulBuilder(
          builder: (context, setState) {
            return AlertDialog(
              title: const Text('Modifier l\'utilisateur'),
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
                    // Disabled dropdown for the student's role
                    DropdownButtonFormField<int>(
                      value: selectedRoleId,
                      decoration: const InputDecoration(labelText: 'Rôle'),
                      items: roles.map<DropdownMenuItem<int>>((role) {
                        return DropdownMenuItem<int>(
                          value: role['id_role'],
                          child: Text(role['name_role']),
                        );
                      }).toList(),
                      onChanged: null, // Grayed out
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
                  child: const Text('Cancel'),
                ),
                TextButton(
                  onPressed: () {
                    if (nameController.text.isEmpty ||
                        lastnameController.text.isEmpty ||
                        emailController.text.isEmpty ||
                        addressController.text.isEmpty) {
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
                        currentActivated,
                        selectedRoleId,
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

  /// Show a dialog to add a new user, automatically assigning the "Student" role
  /// and disabling the dropdown so the user can't change it.
  void showAddUserDialog() {
    final TextEditingController nameController = TextEditingController();
    final TextEditingController lastnameController = TextEditingController();
    final TextEditingController emailController = TextEditingController();
    final TextEditingController addressController = TextEditingController();

    String? errorMessage;
    // By default, the user is assigned the "Student" role
    int? selectedRoleId = studentRoleId;

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
                  const SizedBox(height: 12),
                  // Disabled dropdown listing all roles
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
                      selectedRoleId, // The "Student" role
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
    return Scaffold(
      // We already have an AppBar in the parent widget
      body: Column(
        children: [
          // Search bar to filter the user list
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
          // Expanded list of active students
          Expanded(
            child: ListView.builder(
              itemCount: filteredUsers.length,
              itemBuilder: (context, index) {
                final user = filteredUsers[index];
                // Display the role name or "Unknown Role"
                final roleName =
                    user['role_user']?['name_role'] ?? 'Unknown Role';

                // Extract the user's base64-encoded image
                final String? base64Image = user['picture_user'];
                // Convert base64 to Uint8List if present
                Uint8List? imageBytes = (base64Image != null && base64Image.trim().isNotEmpty)
                    ? base64Decode(base64Image)
                    : null;

                return ListTile(
                  // Tapping the image leads to a dialog showing a bigger picture
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
      // Two FloatingActionButtons at the bottom-right
      floatingActionButton: Row(
        mainAxisAlignment: MainAxisAlignment.end,
        children: [
          // FAB for importing CSV
          FloatingActionButton(
            heroTag: 'importCsv',
            backgroundColor: Colors.orange,
            onPressed: () => showImportCSVDialog(context),
            child: const Icon(Icons.upload_file, color: Colors.white),
          ),
          const SizedBox(width: 16),
          // FAB for adding a new user
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
