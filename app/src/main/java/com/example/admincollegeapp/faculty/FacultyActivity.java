package com.example.admincollegeapp.faculty;

import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.admincollegeapp.R;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.List;

public class FacultyActivity extends AppCompatActivity {
    FloatingActionButton floatingActionButton;
    DatabaseReference databaseReference;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_faculty);

        floatingActionButton = findViewById(R.id.floatingActionButton);
        databaseReference = FirebaseDatabase.getInstance().getReference().child("teacher");

        floatingActionButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(FacultyActivity.this, AddTeachersActivity.class));
            }
        });

        loadTeacherData();
    }

    private void loadTeacherData() {
        databaseReference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                LinearLayout parentLayout = findViewById(R.id.parentLayout);
                parentLayout.removeAllViews(); // Clear previous views

                for (DataSnapshot departmentSnapshot : dataSnapshot.getChildren()) {
                    String departmentName = departmentSnapshot.getKey();
                    List<TeacherData> teachers = new ArrayList<>();
                    for (DataSnapshot teacherSnapshot : departmentSnapshot.getChildren()) {
                        // Convert the DataSnapshot to TeacherData object
                        String key = teacherSnapshot.getKey();
                        String name = teacherSnapshot.child("name").getValue(String.class);
                        String email = teacherSnapshot.child("email").getValue(String.class);
                        String post = teacherSnapshot.child("post").getValue(String.class);
                        String image = teacherSnapshot.child("image").getValue(String.class);
                        String category = teacherSnapshot.child("category").getValue(String.class);
                        // Create a new TeacherData object
                        TeacherData teacher = new TeacherData(name, email, post, image, category, key);

                        teachers.add(teacher);
                    }
                    displayDepartmentWithTeachers(parentLayout, departmentName, teachers);
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {
                // Handle onCancelled
            }
        });
    }

    private void displayDepartmentWithTeachers(LinearLayout parentLayout, String departmentName, List<TeacherData> teachers) {
        View departmentLayout = LayoutInflater.from(this).inflate(R.layout.department_layout, parentLayout, false);

        TextView departmentTextView = departmentLayout.findViewById(R.id.departmentTextView);
        departmentTextView.setText(departmentName);

        RecyclerView recyclerView = departmentLayout.findViewById(R.id.recyclerView);
        LinearLayoutManager layoutManager = new LinearLayoutManager(this);
        recyclerView.setLayoutManager(layoutManager);

        TeacherAdapter adapter = new TeacherAdapter(teachers, this);
        recyclerView.setAdapter(adapter);

        parentLayout.addView(departmentLayout);
    }
}
