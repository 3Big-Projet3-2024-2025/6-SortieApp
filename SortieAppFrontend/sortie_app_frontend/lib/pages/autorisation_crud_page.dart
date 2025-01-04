import 'package:flutter/material.dart';
import 'package:flutter/foundation.dart';
import 'package:http/http.dart' as http;
import 'dart:convert';
import '../utils/backendRequest.dart';
import '../utils/router.dart';

class AutorisationCrudPage extends StatelessWidget {
  const AutorisationCrudPage({super.key});

  @override
  Widget build(BuildContext context) {
    return Scaffold(
      appBar: AppBar(
        title: const Text('Autorisations Management'),
        backgroundColor: Color(0xFF87CEEB),
        actions: [
          IconButton(
            icon: const Icon(Icons.logout),
            onPressed: () async {
              redirectHome();
            },
          ),
        ],
      ),
      body: AutorisationListScreen(),
    );
  }
}

class AutorisationListScreen extends StatefulWidget {
  const AutorisationListScreen({super.key});

  @override
  _AutorisationListScreenState createState() => _AutorisationListScreenState();
}

class _AutorisationListScreenState extends State<AutorisationListScreen> {

  late String apiUrl;
  List autorisations = [];
  List ShowedAutorisations = [];

  @override
  void initState() {
    super.initState();
    apiUrl = '${getBackendUrl()}/Autorisations';
    fetchAutorisations();
  }

  Future<void> fetchAutorisations({int page = 0}) async {
    try {
      var header = await getHeader();
      final response = await http.get(
        Uri.parse(apiUrl),headers: header,
      );

      if (response.statusCode == 200) {
        final data = json.decode(response.body);
        setState(() {
          autorisations = data;
          ShowedAutorisations = List.from(data);
        });
      } else {
        throw Exception('Failed to load autorisations');
      }
    } catch (e) {
      print('Error fetching autorisations: $e');
    }
  }

  Future<void> addOrUpdateAutorisation({
    int? id,
    required String type,
    String? note,
    String? dateDebut,
    String? dateFin,
    String? heureDebut,
    String? heureFin,
    String? jours,
    int? userId,
  }) async {
    try {
      if (type == 'Unique') {
        jours = null;
        dateFin = null;
      } else if (type == 'Daily') {
        jours = null;
      }

      final body = {
        if (id != null) 'id': id,
        'type': type,
        'note': note,
        'date_debut': dateDebut,
        'date_fin': dateFin,
        'heure_debut': heureDebut,
        'heure_fin': heureFin,
        'jours': jours,
        'user': {'id': userId},
      };

      print("Request Body: ${json.encode(body)}");
      final header = await getHeader();
      final uri = Uri.parse(apiUrl);
      final response = id != null
          ? await http.put(
        uri,
        headers: header,
        body: json.encode(body),
      )
          : await http.post(
        uri,
        headers: header,
        body: json.encode(body),
      );

      if (response.statusCode == 200 || response.statusCode == 201) {
        print("Autorisation sauvegardée avec succès !");
        fetchAutorisations();
      } else {
        print("Erreur HTTP ${response.statusCode}: ${response.body}");
        throw Exception(
            'Failed to save autorisation, HTTP ${response.statusCode}');
      }
    } catch (e) {
      print('Error saving autorisation: $e');
    }
  }

  Future<void> deleteAutorisation(int id) async {
    try {
      final header = await getHeader();
      final response = await http.delete(Uri.parse('$apiUrl/$id'),headers: header);
      if (response.statusCode == 200 || response.statusCode == 201 ||
          response.statusCode == 204) {
        fetchAutorisations();
      }
    } catch (e) {
      print('Error deleting autorisation: $e');
    }
  }

  void showAddEditDialog({Map? autorisation}) {
    final noteController = TextEditingController(
        text: autorisation?['note'] ?? '');
    final dateDebutController = TextEditingController(
        text: autorisation?['date_debut']?.split('T')[0] ?? '');
    final dateFinController = TextEditingController(
        text: autorisation?['date_fin']?.split('T')[0] ?? '');
    final heureDebutController = TextEditingController(
        text: autorisation?['heure_debut'] ?? '');
    final heureFinController = TextEditingController(
        text: autorisation?['heure_fin'] ?? '');

    String selectedType = autorisation?['type'] ?? 'Unique';
    String selectedDays = autorisation?['jours'] ?? '';
    List<String> days = [
      'Monday',
      'Tuesday',
      'Wednesday',
      'Thursday',
      'Friday'
    ];
    Map<String, bool> daysSelected = {
      for (var day in days) day: selectedDays.contains(day),
    };

    showDialog(
      context: context,
      builder: (context) {
        return StatefulBuilder(
          builder: (context, setState) {
            return AlertDialog(
              title: Text(autorisation != null
                  ? 'Edit Autorisation'
                  : 'Add Autorisation'),
              content: SingleChildScrollView(
                child: Column(
                  mainAxisSize: MainAxisSize.min,
                  children: [
                    DropdownButtonFormField<String>(
                      value: selectedType,
                      items: ['Unique', 'Daily', 'Weekly']
                          .map((type) =>
                          DropdownMenuItem(
                            value: type,
                            child: Text(type),
                          ))
                          .toList(),
                      onChanged: (value) {
                        setState(() {
                          selectedType = value!;
                          if (selectedType == 'Unique') {
                            dateFinController.clear();
                            selectedDays = '';
                            daysSelected.updateAll((key, value) => false);
                          } else if (selectedType == 'Daily') {
                            selectedDays = '';
                            daysSelected.updateAll((key, value) => false);
                          }
                        });
                      },
                      decoration: InputDecoration(labelText: 'Type'),
                    ),
                    TextField(
                      controller: noteController,
                      decoration: InputDecoration(labelText: 'Note'),
                    ),
                    TextField(
                      controller: dateDebutController,
                      readOnly: true,
                      onTap: () async {
                        DateTime? pickedDate = await showDatePicker(
                          context: context,
                          initialDate: DateTime.now(),
                          firstDate: DateTime(2000),
                          lastDate: DateTime(2100),
                        );
                        if (pickedDate != null) {
                          setState(() {
                            dateDebutController.text =
                            '${pickedDate.year}-${pickedDate.month.toString()
                                .padLeft(2, '0')}-${pickedDate.day.toString()
                                .padLeft(2, '0')}';
                          });
                        }
                      },
                      decoration: InputDecoration(labelText: 'Start Date'),
                    ),
                    if (selectedType != 'Unique')
                      TextField(
                        controller: dateFinController,
                        readOnly: true,
                        onTap: () async {
                          DateTime? pickedDate = await showDatePicker(
                            context: context,
                            initialDate: DateTime.now(),
                            firstDate: DateTime(2000),
                            lastDate: DateTime(2100),
                          );
                          if (pickedDate != null) {
                            setState(() {
                              dateFinController.text =
                              '${pickedDate.year}-${pickedDate.month.toString()
                                  .padLeft(2, '0')}-${pickedDate.day.toString()
                                  .padLeft(2, '0')}';
                            });
                          }
                        },
                        decoration: InputDecoration(labelText: 'End Date'),
                      ),
                    TextField(
                      controller: heureDebutController,
                      readOnly: true,
                      onTap: () async {
                        TimeOfDay? pickedTime = await showTimePicker(
                          context: context,
                          initialTime: TimeOfDay.now(),
                        );
                        if (pickedTime != null) {
                          setState(() {
                            heureDebutController.text =
                            '${pickedTime.hour.toString().padLeft(
                                2, '0')}:${pickedTime.minute.toString().padLeft(
                                2, '0')}';
                          });
                        }
                      },
                      decoration: InputDecoration(labelText: 'Start Time'),
                    ),
                    TextField(
                      controller: heureFinController,
                      readOnly: true,
                      onTap: () async {
                        TimeOfDay? pickedTime = await showTimePicker(
                          context: context,
                          initialTime: TimeOfDay.now(),
                        );
                        if (pickedTime != null) {
                          setState(() {
                            heureFinController.text =
                            '${pickedTime.hour.toString().padLeft(
                                2, '0')}:${pickedTime.minute.toString().padLeft(
                                2, '0')}';
                          });
                        }
                      },
                      decoration: InputDecoration(labelText: 'End Time'),
                    ),
                    if (selectedType == 'Weekly')
                      Wrap(
                        children: days
                            .map((day) =>
                            ChoiceChip(
                              label: Text(day),
                              selected: daysSelected[day]!,
                              onSelected: (selected) {
                                setState(() {
                                  daysSelected[day] = selected;
                                  selectedDays = daysSelected.entries
                                      .where((entry) => entry.value)
                                      .map((entry) => entry.key)
                                      .join(',');
                                });
                              },
                            ))
                            .toList(),
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
                    addOrUpdateAutorisation(
                      id: autorisation?['id'],
                      type: selectedType,
                      note: noteController.text,
                      dateDebut: dateDebutController.text.isEmpty
                          ? null
                          : dateDebutController.text,
                      dateFin: dateFinController.text.isEmpty
                          ? null
                          : dateFinController.text,
                      heureDebut: heureDebutController.text.isEmpty
                          ? null
                          : heureDebutController.text,
                      heureFin: heureFinController.text.isEmpty
                          ? null
                          : heureFinController.text,
                      jours: selectedType == 'Weekly' ? selectedDays : null,
                      userId: 2,
                    );
                    Navigator.pop(context);
                  },
                  child: const Text('Save'),
                ),
              ],
            );
          },
        );
      },
    );
  }

  String buildDisplayInfo(Map<String, dynamic> autorisation) {
    String type = autorisation['type'];
    String note = autorisation['note'] ?? "No notes";
    String dateDebut = autorisation['date_debut']?.split('T')[0] ?? '';
    String dateFin = (type != 'Unique' && autorisation['date_fin'] != null)
        ? autorisation['date_fin']?.split('T')[0] ?? ''
        : '';
    String hours = autorisation['heure_debut'] != null
        ? autorisation['heure_fin'] != null
        ? "${autorisation['heure_debut']} - ${autorisation['heure_fin']}"
        : "${autorisation['heure_debut']}"
        : "No hours specified";
    String joursText = '';
    if (type == 'Weekly' && autorisation['jours'] != null && autorisation['jours'].isNotEmpty) {
      joursText = '\nDays: ${autorisation['jours']}';
    }
    // Récupération du nom et prénom de l'utilisateur
    String userName = '';
    if (autorisation['user'] != null) {
      String firstName = autorisation['user']['name_user'] ?? '';
      String lastName = autorisation['user']['lastname_user'] ?? '';
      userName = '$firstName $lastName';
    }

    return "$type: $note\nStart Date: $dateDebut${dateFin.isNotEmpty ? ' to $dateFin' : ''}\nHours: $hours$joursText\nStudent: $userName";
  }

  @override
  Widget build(BuildContext context) {
    return Scaffold(
      body: ListView.builder(
        itemCount: autorisations.length,
        itemBuilder: (context, index) {
          final autorisation = autorisations[index];
          return Card(
            child: ListTile(
              title: Text(buildDisplayInfo(autorisation)),
              trailing: Row(
                mainAxisSize: MainAxisSize.min,
                children: [
                  IconButton(
                    icon: Icon(Icons.edit, color: Colors.blue),
                    onPressed: () => showAddEditDialog(autorisation: autorisation),
                  ),
                  IconButton(
                    icon: Icon(Icons.delete, color: Colors.red),
                    onPressed: () => deleteAutorisation(autorisation['id']),
                  ),
                ],
              ),
            ),
          );
        },
      ),
      floatingActionButton: FloatingActionButton(
        onPressed: () => showAddEditDialog(),
        child: Icon(Icons.add),
        backgroundColor: Colors.blue,
      ),
    );
  }


}

