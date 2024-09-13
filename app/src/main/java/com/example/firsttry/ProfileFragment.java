package com.example.firsttry;

import android.app.Activity;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;

import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.Toast;

import com.example.firsttry.enums.UploadRequestType;
import com.example.firsttry.enums.UserRole;
import com.example.firsttry.extensions.ValidatedEditText;
import com.example.firsttry.models.ProfilePicture;
import com.example.firsttry.utilities.AccountManager;
import com.example.firsttry.utilities.ActivityHandler;
import com.example.firsttry.utilities.Array;
import com.example.firsttry.utilities.FragmentHandler;
import com.example.firsttry.utilities.GlideHelper;
import com.example.firsttry.utilities.ImageUploader;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.storage.UploadTask;

import java.util.Objects;
import java.util.concurrent.CompletableFuture;

public class ProfileFragment extends Fragment
{
    private ValidatedEditText _username;
    private ValidatedEditText _bio;
    private ImageUploader imageUploader;
    private Button seeBookersButton;
    private View _currentView;
    private ActivityResultLauncher<String> mGetContent;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        _currentView = inflater.inflate(R.layout.fragment_profile, container, false);
        setProfileImage();
        setupButtons();
        setLogoutBtnListener();
        setFields();

        mGetContent = registerForActivityResult(new ActivityResultContracts.GetContent(), uri -> {
            if (uri != null) {
                handleImage(uri);
            } else {
                Toast.makeText(requireActivity(), "Immagine non caricata", Toast.LENGTH_SHORT).show();
            }
        });

        return _currentView;
    }

    private void setFields()
    {
        Objects.requireNonNull(AccountManager.getCurrentAccount())
                .thenAccept(user -> {
                    _username = _currentView.findViewById(R.id.edit_username);
                    _username.setText(user.getUsername());
                    _bio = _currentView.findViewById(R.id.edit_bio);
                    _bio.setText(user.getBio());
                });
    }

    private void setProfileImage() {
        ImageView imageView = _currentView.findViewById(R.id.profile_picture);
        getProfilePicture()
                .thenAccept(pic -> {
                    if (pic == null)
                        return;
                    ImageUploader.getDownloadUrl(pic.getUrl())
                            .addOnSuccessListener(uri -> GlideHelper.setImage(
                                    imageView,
                                    uri.toString(),
                                    requireContext()
                            ));
                });
    }

    private CompletableFuture<ProfilePicture> getProfilePicture()
    {
        String userId = FirebaseAuth.getInstance().getCurrentUser().getUid();
        return ProfilePicture.list(pic -> pic.getUserId().equals(userId))
                .thenApply(Array::firstOrDefault);
    }

    private void setupButtons()
    {
        setSaveBtnListener();
        setEditImageBtnListener();
        setSeeBookedLessonsBtnListener();
        setSeeBookedCourtsBtnListener();
        setSeeInvitationsBtnListener();
        setSeeBookersBtnListener();
    }

    private void setSaveBtnListener()
    {
        Button saveBtn = _currentView.findViewById(R.id.btn_save_profile);
        saveBtn.setOnClickListener(v -> saveUser());
    }

    private void setEditImageBtnListener()
    {
        Button editImageBtn = _currentView.findViewById(R.id.btn_edit_profile_image);
        editImageBtn.setOnClickListener(v -> openFileChooser());
    }

    private void setSeeBookedLessonsBtnListener()
    {
        Button seeBookedLessonsButton = _currentView.findViewById(R.id.btn_see_lessonbooks);
        seeBookedLessonsButton.setOnClickListener(v -> FragmentHandler.replaceFragment(requireActivity(), new SeeBookedLessonsFragment()));
    }

    private void setSeeBookedCourtsBtnListener()
    {
        Objects.requireNonNull(AccountManager.getCurrentAccount()).thenAccept(user -> {
            Button seeBookedCourtsButton = _currentView.findViewById(R.id.btn_see_courtbooks);

            if (user.getRole().equals(UserRole.Admin))
            {
                seeBookedCourtsButton.setVisibility(View.GONE);
                return;
            }

            seeBookedCourtsButton.setOnClickListener(v -> FragmentHandler.replaceFragment(requireActivity(), new SeeBookedCourtsFragment()));
        });
    }

    private void setSeeInvitationsBtnListener()
    {
        Button seeInvitationsButton = _currentView.findViewById(R.id.btn_see_invitations);
        seeInvitationsButton.setOnClickListener(v -> FragmentHandler.replaceFragment(requireActivity(), new SeeInvitationsFragment()));
    }

    private void setSeeBookersBtnListener()
    {
        Objects.requireNonNull(AccountManager.getCurrentAccount()).thenAccept(user -> {
            seeBookersButton = _currentView.findViewById(R.id.btn_see_bookers);

            if (!user.getRole().equals(UserRole.Teacher))
            {
                seeBookersButton.setVisibility(View.GONE);
                return;
            }

            seeBookersButton.setVisibility(View.VISIBLE);
            seeBookersButton.setOnClickListener(v -> FragmentHandler.replaceFragment(requireActivity(), new SeeBookersFragment()));
        });
    }

    private void setLogoutBtnListener()
    {
        Button logoutBtn = _currentView.findViewById(R.id.btn_logout);
        logoutBtn.setOnClickListener(v -> doLogout());
    }

    private CompletableFuture<ProfilePicture> saveProfilePicture(String imageName)
    {
        String userId = FirebaseAuth.getInstance().getCurrentUser().getUid();
        ProfilePicture pic = new ProfilePicture("profilepictures/" + imageName, userId);
        return pic.save();
    }

    private void doLogout()
    {
        AccountManager.doLogout();
        startActivity(new Intent(getActivity(), LoginActivity.class));
        requireActivity().finish();
    }

    private void openFileChooser() {
        mGetContent.launch("image/*");
    }

    private void handleImage(Uri imageUri) {
        String userId = FirebaseAuth.getInstance().getCurrentUser().getUid();
        String extension = ImageUploader.getFileExtension(imageUri, this);

        imageUploader = new ImageUploader(requireActivity(), UploadRequestType.ProfilePictures);
        UploadTask res = imageUploader.uploadImageToFirebase(imageUri, userId + "." + extension);

        if (res != null) {
            res.addOnSuccessListener(taskSnapshot -> saveProfilePicture(userId + "." + extension)
                            .thenAccept(pic -> Toast.makeText(requireActivity(), "Immagine caricata con successo", Toast.LENGTH_SHORT).show()))
                    .addOnFailureListener(e -> Toast.makeText(requireActivity(), "Upload fallito: " + e.getMessage(), Toast.LENGTH_SHORT).show());
        }
    }

    private void saveUser()
    {
        Objects.requireNonNull(AccountManager.getCurrentAccount()).thenAccept(user -> {
            user.setUsername(_username.getText().toString());
            user.setBio(_bio.getText().toString());
            user.save().thenAccept(res -> Toast.makeText(
                    getActivity(),
                    "Modifiche salvate con successo!",
                    Toast.LENGTH_SHORT).show());
        });
    }
}
