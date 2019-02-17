package com.vladislavmyasnikov.main_android_components;

import android.view.LayoutInflater;
import android.view.ViewGroup;

import com.vladislavmyasnikov.main_android_components.databinding.ListItemContactBinding;

import java.util.ArrayList;

import androidx.databinding.DataBindingUtil;
import androidx.recyclerview.widget.RecyclerView;

public class ContactAdapter extends RecyclerView.Adapter<ContactAdapter.ContactViewHolder> {

    public ContactAdapter(ArrayList<ContactData> contacts) {
        mContacts = contacts;
    }

    @Override
    public ContactViewHolder onCreateViewHolder(ViewGroup parent, int typeView) {
        LayoutInflater inflater = LayoutInflater.from(parent.getContext());
        ListItemContactBinding binding = DataBindingUtil.inflate(inflater, R.layout.list_item_contact, parent, false);
        return new ContactViewHolder(binding);
    }

    @Override
    public void onBindViewHolder(ContactViewHolder holder, int position) {
        ContactData contact = mContacts.get(position);
        holder.mBinding.nameField.setText(contact.getDisplayName());
        holder.mBinding.phoneNumbersField.setText(contact.getPhoneNumbersAsString());
    }

    @Override
    public int getItemCount() {
        return mContacts.size();
    }

    static class ContactViewHolder extends RecyclerView.ViewHolder {
        private ListItemContactBinding mBinding;

        ContactViewHolder(ListItemContactBinding binding) {
            super(binding.getRoot());
            mBinding = binding;
        }
    }

    private ArrayList<ContactData> mContacts;
}