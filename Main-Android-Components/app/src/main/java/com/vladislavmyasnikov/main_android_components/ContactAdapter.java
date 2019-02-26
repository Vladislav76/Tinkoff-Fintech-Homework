package com.vladislavmyasnikov.main_android_components;

import android.view.LayoutInflater;
import android.view.ViewGroup;

import com.vladislavmyasnikov.main_android_components.databinding.ListItemContactBinding;

import java.util.List;

import androidx.annotation.NonNull;
import androidx.databinding.DataBindingUtil;
import androidx.recyclerview.widget.RecyclerView;

public class ContactAdapter extends RecyclerView.Adapter<ContactAdapter.ContactViewHolder> {

    public void setContacts(List<ContactData> contacts) {
        mContacts = contacts;
    }

    @Override
    @NonNull
    public ContactViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int typeView) {
        LayoutInflater inflater = LayoutInflater.from(parent.getContext());
        ListItemContactBinding binding = DataBindingUtil.inflate(inflater, R.layout.list_item_contact, parent, false);
        return new ContactViewHolder(binding);
    }

    @Override
    public void onBindViewHolder(@NonNull ContactViewHolder holder, int position) {
        ContactData contact = mContacts.get(position);
        holder.mBinding.nameField.setText(contact.getDisplayName());
        holder.mBinding.phoneNumbersField.setText(getPhoneNumbersAsString(contact));
    }

    @Override
    public int getItemCount() {
        return mContacts.size();
    }

    private String getPhoneNumbersAsString(ContactData contact) {
        if (contact.getPhoneNumbers().size() > 0) {
            return contact.getPhoneNumbers().toString();
        }
        return EMPTY_PHONE_NUMBERS;
    }

    static class ContactViewHolder extends RecyclerView.ViewHolder {
        private ListItemContactBinding mBinding;

        ContactViewHolder(ListItemContactBinding binding) {
            super(binding.getRoot());
            mBinding = binding;
        }
    }

    private List<ContactData> mContacts;

    private static final String EMPTY_PHONE_NUMBERS = "No phone numbers";
}