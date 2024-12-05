import 'package:flutter/material.dart';
import 'package:http/http.dart' as http;
import 'dart:convert';

void main() {
  runApp(const UserApp());
}

class UserApp extends StatelessWidget {
  const UserApp({super.key});

  @override
  Widget build(BuildContext context) {
    return MaterialApp(
      title: 'User Management',
      theme: ThemeData(
        primarySwatch: Colors.blue,
      ),
      home: const UserListScreen(),
    );
  }
}

class UserListScreen extends StatefulWidget {
  const UserListScreen({super.key});

  @override
  _UserListScreenState createState() => _UserListScreenState();
}

class _UserListScreenState extends State<UserListScreen> {
  final String apiUrl = 'http://10.0.2.2:8081/users'; // Backend API URL
  //final String apiUrl = 'http://localhost:8081/users';
  List users = [];

  @override
  void initState() {
    super.initState();
    fetchUsers();
  }

  // Fetch the list of users from the backend
  Future<void> fetchUsers() async {
    try {
      final response = await http.get(Uri.parse(apiUrl));
      if (response.statusCode == 200) {
        setState(() {
          users = json.decode(response.body);
        });
      } else {
        throw Exception('Failed to load users');
      }
    } catch (e) {
      print('Error: $e');
    }
  }

  // Add a new user to the backend
  Future<void> addUser(String name_user, String lastname_user, String email_user, String address_user) async {
    try {
      final response = await http.post(
        Uri.parse(apiUrl),
        headers: {'Content-Type': 'application/json'},
        body: json.encode({
          'name_user': name_user,
          'lastname_user': lastname_user,
          'email_user': email_user,
          'address_user': address_user,
        }),
      );
      if (response.statusCode == 200) {
        fetchUsers(); // Refresh the user list after adding
      }
    } catch (e) {
      print('Error: $e');
    }
  }

  // Update a user's data
  Future<void> updateUser(int idUser, String name_user, String lastname_user, String email_user, String address_user) async {
    try {
      final response = await http.put(
        Uri.parse('$apiUrl/$idUser'),
        headers: {'Content-Type': 'application/json'},
        body: json.encode({
          'name_user': name_user,
          'lastname_user': lastname_user,
          'email_user': email_user,
          'address_user': address_user,
        }),
      );
      if (response.statusCode == 200) {
        fetchUsers(); // Refresh the user list after updating
      }
    } catch (e) {
      print('Error: $e');
    }
  }

  // Delete a user
  Future<void> deleteUser(int idUser) async {
    try {
      final response = await http.delete(Uri.parse('$apiUrl/$idUser'));
      if (response.statusCode == 200) {
        fetchUsers(); // Refresh the user list after deleting
      }
    } catch (e) {
      print('Error: $e');
    }
  }

  // Show a dialog to add a new user
  void showAddUserDialog() {
    final TextEditingController nameController = TextEditingController();
    final TextEditingController lastnameController = TextEditingController();
    final TextEditingController emailController = TextEditingController();
    final TextEditingController addressController = TextEditingController();

    showDialog(
      context: context,
      builder: (context) {
        return AlertDialog(
          title: const Text('Add User'),
          content: Column(
            mainAxisSize: MainAxisSize.min,
            children: [
              TextField(
                controller: nameController,
                decoration: const InputDecoration(hintText: 'Enter name'),
              ),
              TextField(
                controller: lastnameController,
                decoration: const InputDecoration(hintText: 'Enter lastname'),
              ),
              TextField(
                controller: emailController,
                decoration: const InputDecoration(hintText: 'Enter email'),
              ),
              TextField(
                controller: addressController,
                decoration: const InputDecoration(hintText: 'Enter address'),
              ),
            ],
          ),
          actions: [
            TextButton(
              onPressed: () => Navigator.pop(context), // Close the dialog
              child: const Text('Cancel'),
            ),
            TextButton(
              onPressed: () {
                addUser(nameController.text, lastnameController.text, emailController.text, addressController.text);
                Navigator.pop(context);
              },
              child: const Text('Add'),
            ),
          ],
        );
      },
    );
  }

  // Show a dialog to edit an existing user
  void showEditUserDialog(int idUser, String currentName, String currentLastname, String currentEmail, String currentAddress) {
    final TextEditingController nameController = TextEditingController();
    final TextEditingController lastnameController = TextEditingController();
    final TextEditingController emailController = TextEditingController();
    final TextEditingController addressController = TextEditingController();

    nameController.text = currentName;
    lastnameController.text = currentLastname;
    emailController.text = currentEmail;
    addressController.text = currentAddress;

    showDialog(
      context: context,
      builder: (context) {
        return AlertDialog(
          title: const Text('Edit User'),
          content: Column(
            mainAxisSize: MainAxisSize.min,
            children: [
              TextField(
                controller: nameController,
                decoration: const InputDecoration(hintText: 'Enter name'),
              ),
              TextField(
                controller: lastnameController,
                decoration: const InputDecoration(hintText: 'Enter lastname'),
              ),
              TextField(
                controller: emailController,
                decoration: const InputDecoration(hintText: 'Enter email'),
              ),
              TextField(
                controller: addressController,
                decoration: const InputDecoration(hintText: 'Enter address'),
              ),
            ],
          ),
          actions: [
            TextButton(
              onPressed: () => Navigator.pop(context), // Close the dialog
              child: const Text('Cancel'),
            ),
            TextButton(
              onPressed: () {
                updateUser(idUser, nameController.text, lastnameController.text, emailController.text, addressController.text);
                Navigator.pop(context);
              },
              child: const Text('Update'),
            ),
          ],
        );
      },
    );
  }

  // Main UI for the screen
  @override
  Widget build(BuildContext context) {
    return Scaffold(
      appBar: AppBar(
        title: const Text('User Management'),
      ),
      body: ListView.builder(
        itemCount: users.length,
        itemBuilder: (context, index) {
          final user = users[index];

          // Safely extract values or use empty strings if null
          final name = user['name_user'] ?? '';
          final lastname = user['lastname_user'] ?? '';
          final email = user['email_user'] ?? '';
          final address = user['address_user'] ?? '';

          return ListTile(
            title: Text('$name $lastname'),
            subtitle: Text('$email\n$address'),
            trailing: Row(
              mainAxisSize: MainAxisSize.min,
              children: [
                IconButton(
                  icon: const Icon(Icons.edit),
                  onPressed: () => showEditUserDialog(
                    user['id_user'],
                    name,
                    lastname,
                    email,
                    address,
                  ),
                ),
                IconButton(
                  icon: const Icon(Icons.delete),
                  onPressed: () => deleteUser(user['id_user']),
                ),
              ],
            ),
          );
        },
      ),
      floatingActionButton: FloatingActionButton(
        onPressed: showAddUserDialog, // Show the add user dialog
        child: const Icon(Icons.add),
      ),
    );
  }
}
