package com.example.myapplication;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.net.Uri;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.content.Context;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import java.util.ArrayList;

public class Adapter extends RecyclerView.Adapter<Adapter.Holder> {
    private Context context;
    private ArrayList<User> users;
    private DatabaseAdapter databaseAdapter;

    public Adapter(Context context, ArrayList<User> users) {
        this.context = context;
        this.users = users;
    }

    @NonNull
    @Override
    public Holder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        databaseAdapter = new DatabaseAdapter(context);
        View view = LayoutInflater.from(context).inflate(R.layout.content_main, parent, false);
        return new Holder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull Holder holder, final int id) {
        final User user = users.get(id);

        holder.profileImageView.setImageURI(Uri.parse(user.getUserImage()));
        holder.name.setText(user.getFirstName() + " " + user.getLastName());
        holder.age.setText("Age: " + user.getAge());
        holder.phone.setText("Phone number: " + user.getPhoneNumber());
        holder.editButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(context, EditUserActivity.class);
                intent.putExtra("ID", user.getId());

                context.startActivity(intent);
            }
        });

        holder.itemView.setOnLongClickListener(new View.OnLongClickListener() {
            @Override
            public boolean onLongClick(View view) {
                AlertDialog.Builder builder = new AlertDialog.Builder(context);
                builder.setTitle("Are you sure to delete?");
                builder.setCancelable(false);
                builder.setPositiveButton("Yes", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {
                        databaseAdapter.open();
                        databaseAdapter.delete(id);
                        databaseAdapter.close();

                        ((MainActivity)context).onResume();
                        Toast.makeText(context, "Item was deleted successfully", Toast.LENGTH_SHORT).show();
                    }
                });
                builder.setNegativeButton("No", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {
                        dialogInterface.cancel();
                    }
                });

                builder.create().show();
                return false;
            }
        });
    }

    @Override
    public int getItemCount() {
        return users.size();
    }

    class Holder extends RecyclerView.ViewHolder {
        ImageView profileImageView;
        TextView name, age, phone;
        ImageButton editButton;

        public Holder(@NonNull View itemView) {
            super(itemView);

            profileImageView = (ImageView) itemView.findViewById(R.id.profileImage);
            name = (TextView) itemView.findViewById(R.id.name);
            age = (TextView) itemView.findViewById(R.id.age);
            phone = (TextView) itemView.findViewById(R.id.phone_number);
            editButton = (ImageButton) itemView.findViewById(R.id.edit_button);
        }
    }
}
