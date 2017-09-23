package com.example.varda.naviraklio.adapters;

import android.support.v7.widget.AppCompatTextView;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.example.varda.naviraklio.R;
import com.example.varda.naviraklio.model.User;

import java.util.List;

public class UsersRecyclerAdapter extends RecyclerView.Adapter<UsersRecyclerAdapter.UserViewHolder> {

    private List<User> listUsers;

    public UsersRecyclerAdapter(List<User> listUsers) {
        this.listUsers = listUsers;
    }

    @Override
    public UserViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        // inflating recycler item view
        View itemView = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.item_user_recycler, parent, false);

        return new UserViewHolder(itemView);
    }

    @Override
    public void onBindViewHolder(UserViewHolder holder, int position) {
        holder.textViewName.setText(listUsers.get(position).getName());
        holder.textViewUsername.setText(listUsers.get(position).getUsername());
        holder.textViewPassword.setText(listUsers.get(position).getPassword());
        holder.textViewAddress.setText(listUsers.get(position).getAddress());
        holder.textViewTel.setText(listUsers.get(position).getTel());
        holder.textViewCreditCard.setText(listUsers.get(position).getCreditCard());
    }

    @Override
    public int getItemCount() {
        Log.v(UsersRecyclerAdapter.class.getSimpleName(),""+listUsers.size());
        return listUsers.size();
    }


    /**
     * ViewHolder class
     */
    public class UserViewHolder extends RecyclerView.ViewHolder {

        public AppCompatTextView textViewName;
        public AppCompatTextView textViewUsername;
        public AppCompatTextView textViewPassword;
        public AppCompatTextView textViewAddress;
        public AppCompatTextView textViewTel;
        public AppCompatTextView textViewCreditCard;

        public UserViewHolder(View view) {
            super(view);
            textViewName = (AppCompatTextView) view.findViewById(R.id.textViewName);
            textViewUsername = (AppCompatTextView) view.findViewById(R.id.textViewUsername);
            textViewPassword = (AppCompatTextView) view.findViewById(R.id.textViewPassword);
            textViewAddress = (AppCompatTextView) view.findViewById(R.id.textViewAddress);
            textViewTel = (AppCompatTextView) view.findViewById(R.id.textViewTel);
            textViewCreditCard = (AppCompatTextView) view.findViewById(R.id.textViewCreditCard);
        }
    }

}
