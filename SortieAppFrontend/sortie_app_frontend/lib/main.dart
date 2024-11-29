import 'package:flutter/material.dart';
import 'package:http/http.dart' as http;
import 'dart:convert';

void main() {
  runApp(UserApp());
}

// Entry point of the app
class UserApp extends StatelessWidget {
  @override
  Widget build(BuildContext context) {
    return MaterialApp(
      title: 'User Management',
      theme: ThemeData(
        primarySwatch: Colors.blue,
      ),
      home: UserListScreen(),
    );
  }
}

class UserListScreen extends StatefulWidget {
  @override
  _UserListScreenState createState() => _UserListScreenState();
}

class _UserListScreenState extends State<UserListScreen> {
  final String apiUrl = 'http://10.0.2.2:8081/users'; // Backend API URL
  List users = []; // List to store user data

  @override
  void initState() {
    super.initState();
    fetchUsers(); // Load users when the screen initializes
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
  Future<void> addUser(String name, String lastname, String email) async {
    try {
      final response = await http.post(
        Uri.parse(apiUrl),
        headers: {'Content-Type': 'application/json'},
        body: json.encode({
          'name': name,
          'lastname': lastname,
          'email': email,
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
  Future<void> updateUser(int idUser, String name, String lastname,
      String email) async {
    try {
      final response = await http.put(
        Uri.parse('$apiUrl/$idUser'),
        headers: {'Content-Type': 'application/json'},
        body: json.encode({
          'name': name,
          'lastname': lastname,
          'email': email,
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

    showDialog(
      context: context,
      builder: (context) {
        return AlertDialog(
          title: Text('Add User'),
          content: Column(
            mainAxisSize: MainAxisSize.min,
            children: [
              TextField(
                controller: nameController,
                decoration: InputDecoration(hintText: 'Enter name'),
              ),
              TextField(
                controller: lastnameController,
                decoration: InputDecoration(hintText: 'Enter lastname'),
              ),
              TextField(
                controller: emailController,
                decoration: InputDecoration(hintText: 'Enter email'),
              ),
            ],
          ),
          actions: [
            TextButton(
              onPressed: () => Navigator.pop(context), // Close the dialog
              child: Text('Cancel'),
            ),
            TextButton(
              onPressed: () {
                addUser(nameController.text, lastnameController.text,
                    emailController.text);
                Navigator.pop(context);
              },
              child: Text('Add'),
            ),
          ],
        );
      },
    );
  }

  // Show a dialog to edit an existing user
  void showEditUserDialog(int idUser, String currentName,
      String currentLastname, String currentEmail) {
    final TextEditingController nameController = TextEditingController();
    final TextEditingController lastnameController = TextEditingController();
    final TextEditingController emailController = TextEditingController();

    nameController.text = currentName;
    lastnameController.text = currentLastname;
    emailController.text = currentEmail;

    showDialog(
      context: context,
      builder: (context) {
        return AlertDialog(
          title: Text('Edit User'),
          content: Column(
            mainAxisSize: MainAxisSize.min,
            children: [
              TextField(
                controller: nameController,
                decoration: InputDecoration(hintText: 'Enter name'),
              ),
              TextField(
                controller: lastnameController,
                decoration: InputDecoration(hintText: 'Enter lastname'),
              ),
              TextField(
                controller: emailController,
                decoration: InputDecoration(hintText: 'Enter email'),
              ),
            ],
          ),
          actions: [
            TextButton(
              onPressed: () => Navigator.pop(context), // Close the dialog
              child: Text('Cancel'),
            ),
            TextButton(
              onPressed: () {
                updateUser(idUser, nameController.text, lastnameController.text,
                    emailController.text);
                Navigator.pop(context);
              },
              child: Text('Update'),
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
        title: Text('User Management'),
      ),
      body: ListView.builder(
        itemCount: users.length, // Number of items in the list
        itemBuilder: (context, index) {
          final user = users[index]; // Get the current user

          // Safely extract values or use empty strings if null
          final name = user['name'] ?? '';
          final lastname = user['lastname'] ?? '';
          final email = user['email'] ?? '';

          return ListTile(
            title: Text('$name $lastname'),
            subtitle: Text(email),
            trailing: Row(
              mainAxisSize: MainAxisSize.min,
              children: [
                IconButton(
                  icon: Icon(Icons.edit),
                  onPressed: () =>
                      showEditUserDialog(
                        user['id_user'], // Assuming id_user is non-null
                        name,
                        lastname,
                        email,
                      ),
                ),
                IconButton(
                  icon: Icon(Icons.delete),
                  onPressed: () => deleteUser(user['id_user']),
                ),
              ],
            ),
          );
        },
      ),
      floatingActionButton: FloatingActionButton(
        onPressed: showAddUserDialog, // Show the add user dialog
        child: Icon(Icons.add),
      ),
    );
  }
}