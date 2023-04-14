package com.example.smartsavr;

import android.view.LayoutInflater;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.DiffUtil;
import androidx.recyclerview.widget.ListAdapter;
import androidx.recyclerview.widget.RecyclerView;

import com.example.smartsavr.databinding.ProfilePictureItemBinding;

public class ProfilePictureAdapter extends ListAdapter<ProfilePictureItem, ProfilePictureAdapter.ViewHolder> {

    private static int lastCheckedPos = 0;

    public static class ViewHolder extends RecyclerView.ViewHolder {
        final ProfilePictureItemBinding binding;

        public ViewHolder(ProfilePictureItemBinding binding) {
            super(binding.getRoot());
            this.binding = binding;
        }
    }

    public ProfilePictureAdapter() {
        super(DIFF_CALLBACK);
    }

    @Override
    public void onBindViewHolder(ViewHolder holder, int position) {
        holder.binding.profilePictureImageView.setImageResource(getItem(position).getResourceId());

        holder.binding.profilePictureCard.setChecked(position == lastCheckedPos);

        holder.binding.profilePictureCard.setOnClickListener(view -> {
            int oldLastCheckedPos = lastCheckedPos;
            lastCheckedPos = holder.getAdapterPosition();
            notifyItemChanged(oldLastCheckedPos);
            notifyItemChanged(lastCheckedPos);
        });

    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup viewGroup, int viewType) {
        ProfilePictureItemBinding binding = ProfilePictureItemBinding.inflate(LayoutInflater.from(viewGroup.getContext()), viewGroup, false);
        return new ViewHolder(binding);
    }

    public static final DiffUtil.ItemCallback<ProfilePictureItem> DIFF_CALLBACK = new DiffUtil.ItemCallback<ProfilePictureItem>() {
        @Override
        public boolean areItemsTheSame(@NonNull ProfilePictureItem oldItem, @NonNull ProfilePictureItem newItem) {
            return oldItem.getResourceId() == newItem.getResourceId();
        }

        @Override
        public boolean areContentsTheSame(@NonNull ProfilePictureItem oldItem, @NonNull ProfilePictureItem newItem) {
            return oldItem.equals(newItem);
        }
    };
}
