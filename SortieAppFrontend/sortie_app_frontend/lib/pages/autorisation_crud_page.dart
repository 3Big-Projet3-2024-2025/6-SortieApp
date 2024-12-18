import 'package:flutter/material.dart';
import 'package:flutter/foundation.dart';
import 'package:http/http.dart' as http;
import 'dart:convert';
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
  String getBackendUrl() {
    return kIsWeb ? 'http://localhost:8081' : 'http://10.0.2.2:8081';
  }

  late String apiUrl;
  List autorisations = [];

  @override
  void initState() {
    super.initState();
    apiUrl = '${getBackendUrl()}/Autorisations';
    fetchAutorisations();
  }

  Future<void> fetchAutorisations({int page = 0}) async {
    try {
      final response = await http.get(
        Uri.parse(apiUrl),
      );

      if (response.statusCode == 200) {
        final data = json.decode(response.body);
        setState(() {
          autorisations = data;

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
      final url = id != null ? '$apiUrl/$id' : apiUrl;
      final method = id != null ? 'PUT' : 'POST';

      final response = await http.Request(method, Uri.parse(url))
        ..headers.addAll({'Content-Type': 'application/json'})
        ..body = json.encode({
          'type': type,
          'note': note,
          'date_debut': dateDebut,
          'date_fin': dateFin,
          'heure_debut': heureDebut,
          'heure_fin': heureFin,
          'jours': jours,
          'user': {'id': userId},
        });

      final streamedResponse = await response.send();
      if (streamedResponse.statusCode == 200 || streamedResponse.statusCode == 201) {
        fetchAutorisations();
      } else {
        final code = streamedResponse.statusCode;
        throw Exception('Failed to save autorisation $code');
      }
    } catch (e) {
      print('Error saving autorisation: $e');
    }
  }

  Future<void> deleteAutorisation(int id) async {
    try {
      final response = await http.delete(Uri.parse('$apiUrl/$id'));
      if (response.statusCode == 200) {
        fetchAutorisations();
      }
    } catch (e) {
      print('Error deleting autorisation: $e');
    }
  }

  void showAddEditDialog({Map? autorisation}) {
    final TextEditingController noteController = TextEditingController(
        text: autorisation != null ? autorisation['note'] : '');
    final TextEditingController dateDebutController = TextEditingController(
        text: autorisation != null
            ? autorisation['date_debut']?.split('T')[0]
            : '');
    final TextEditingController dateFinController = TextEditingController(
        text:
        autorisation != null ? autorisation['date_fin']?.split('T')[0] : '');
    final TextEditingController heureDebutController = TextEditingController(
        text: autorisation != null ? autorisation['heure_debut'] : '');
    final TextEditingController heureFinController = TextEditingController(
        text: autorisation != null ? autorisation['heure_fin'] : '');

    String? selectedType = autorisation?['type'] ?? 'Unique';
    String selectedDays = autorisation?['jours'] ?? '';
    List<String> days = ['Monday', 'Tuesday', 'Wednesday', 'Thursday', 'Friday'];
    Map<String, bool> daysSelected = {
      for (var day in days) day: selectedDays.contains(day),
    };

    showDialog(
      context: context,
      builder: (context) {
        return StatefulBuilder(
          builder: (context, setState) {
            return AlertDialog(
              title: Text(autorisation != null ? 'Edit Autorisation' : 'Add Autorisation'),
              content: SingleChildScrollView(
                child: Column(
                  mainAxisSize: MainAxisSize.min,
                  children: [
                    DropdownButtonFormField<String>(
                      value: selectedType,
                      items: ['Unique', 'Daily', 'Weekly']
                          .map((type) => DropdownMenuItem(
                        value: type,
                        child: Text(type),
                      ))
                          .toList(),
                      onChanged: (value) {
                        setState(() {
                          selectedType = value;
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
                            '${pickedDate.year}-${pickedDate.month.toString().padLeft(2, '0')}-${pickedDate.day.toString().padLeft(2, '0')}';
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
                              '${pickedDate.year}-${pickedDate.month.toString().padLeft(2, '0')}-${pickedDate.day.toString().padLeft(2, '0')}';
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
                            '${pickedTime.hour.toString().padLeft(2, '0')}:${pickedTime.minute.toString().padLeft(2, '0')}';
                          });
                        }
                      },
                      decoration: InputDecoration(labelText: 'Start Time'),
                    ),
                    if (selectedType != 'Unique')
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
                              '${pickedTime.hour.toString().padLeft(2, '0')}:${pickedTime.minute.toString().padLeft(2, '0')}';
                            });
                          }
                        },
                        decoration: InputDecoration(labelText: 'End Time'),
                      ),
                    if (selectedType == 'Weekly')
                      Wrap(
                        children: days
                            .map(
                              (day) => ChoiceChip(
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
                          ),
                        )
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
                      type: selectedType!,
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
                      userId: 1
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

  @override
  Widget build(BuildContext context) {
    return Scaffold(
      body: ListView.builder(
        itemCount: autorisations.length,
        itemBuilder: (context, index) {
          final autorisation = autorisations[index];

          // Fonction pour formater la date et l'heure
          String formatDateTime(String? dateTime) {
            if (dateTime == null || dateTime.isEmpty) {
              return 'N/A';
            }
            try {
              final date = DateTime.parse(dateTime);
              return '${date.toLocal().toString().split(' ')[0]} at ${date.hour.toString().padLeft(2, '0')}:${date.minute.toString().padLeft(2, '0')}';
            } catch (e) {
              return 'Invalid date';
            }
          }

          return ListTile(
            title: Text('${autorisation['type']}'),
            subtitle: Text('${autorisation['note'] ?? 'No Note'}\n'
                'Start: ${formatDateTime(autorisation['date_debut'])}\n'
                'End: ${formatDateTime(autorisation['date_fin'])}'),
            trailing: Row(
              mainAxisSize: MainAxisSize.min,
              children: [
                IconButton(
                  icon: const Icon(Icons.edit),
                  onPressed: () => showAddEditDialog(autorisation: autorisation),
                ),
                IconButton(
                  icon: const Icon(Icons.delete),
                  onPressed: () => deleteAutorisation(autorisation['id']),
                ),
              ],
            ),
          );
        },
      ),
      floatingActionButton: FloatingActionButton(
        onPressed: () => showAddEditDialog(),
        child: const Icon(Icons.add),
      ),
    );
  }
}

