package com.example.firsttry;

import android.net.Uri;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.Toast;

import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;

import com.example.firsttry.enums.UploadRequestType;
import com.example.firsttry.extensions.ValidatedEditText;
import com.example.firsttry.extensions.ValidatedFragment;
import com.example.firsttry.models.ClubData;
import com.example.firsttry.models.ClubPicture;
import com.example.firsttry.models.ProfilePicture;
import com.example.firsttry.utilities.AccountManager;
import com.example.firsttry.utilities.Array;
import com.example.firsttry.utilities.FragmentHandler;
import com.example.firsttry.utilities.GlideHelper;
import com.example.firsttry.utilities.ImageUploader;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.storage.UploadTask;

import java.util.concurrent.CompletableFuture;

public class ClubSettingsFragment extends ValidatedFragment
{
    private ClubData configuration;

    private ValidatedEditText editName;

    private ActivityResultLauncher<String> mGetContent;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState)
    {
        currentView = inflater.inflate(R.layout.fragment_club_settings, container, false);
        mGetContent = registerForActivityResult(new ActivityResultContracts.GetContent(), uri -> {
            if (uri != null) {
                handleImage(uri);
            } else {
                Toast.makeText(requireActivity(), "Immagine non caricata", Toast.LENGTH_SHORT).show();
            }
        });
        currentConfiguration().thenAccept(config -> {
            configuration = config != null
                    ? config
                    : new ClubData();
            setFields();
            setClubPicture();
        });
        return currentView;
    }

    private CompletableFuture<ClubData> currentConfiguration()
    {
        return ClubData.list().thenApply(Array::firstOrDefault);
    }

    private void setFields()
    {
        editName = currentView.findViewById(R.id.edit_name);
        editName.setText(configuration.getName());
        editName.setRequired(true);

        Button editClubPicButton = currentView.findViewById(R.id.btn_edit_profile_image);
        editClubPicButton.setOnClickListener(v -> openFileChooser());

        Button saveDataButton = currentView.findViewById(R.id.btn_save_profile);
        saveDataButton.setOnClickListener(v -> saveConfiguration());
    }

    private void setClubPicture()
    {
        ImageView imageView = currentView.findViewById(R.id.profile_picture);
        configuration.currentPicture()
                .thenAccept(pic -> {
                    if (pic == null)
                        return;
                    ImageUploader.getDownloadUrl(pic.getUrl())
                            .addOnSuccessListener(uri -> GlideHelper.setRoundedImage(
                                    imageView,
                                    uri.toString(),
                                    requireContext()
                            ));
                });
    }

    private void openFileChooser()
    {
        mGetContent.launch("image/*");
    }

    private CompletableFuture<ClubPicture> saveProfilePicture(String imageName)
    {
        return AccountManager.getCurrentAccount().thenCompose(user -> user.currentProfilePicture().thenCompose(picture -> {
            ClubPicture pic = new ClubPicture("clubpictures/" + imageName);
            if (picture != null)
                pic.setId(picture.getId());
            return pic.save();
        }));
    }

    private void handleImage(Uri imageUri)
    {
        String userId = FirebaseAuth.getInstance().getCurrentUser().getUid();
        String extension = ImageUploader.getFileExtension(imageUri, this);

        ImageUploader imageUploader = new ImageUploader(requireActivity(), UploadRequestType.ClubPictures);
        UploadTask res = imageUploader.uploadImageToFirebase(imageUri, userId + "." + extension);

        if (res != null) {
            res.addOnSuccessListener(taskSnapshot -> saveProfilePicture(userId + "." + extension)
                            .thenAccept(pic -> Toast.makeText(requireActivity(), "Immagine caricata con successo", Toast.LENGTH_SHORT).show()))
                    .addOnFailureListener(e -> Toast.makeText(requireActivity(), "Upload fallito: " + e.getMessage(), Toast.LENGTH_SHORT).show());
        }
    }

    private void saveConfiguration()
    {
        if(!validateFields())
        {
            return;
        }

        configuration.setName(editName.getText().toString());
        configuration.save()
                .thenAccept(res -> {
                    Toast.makeText(requireActivity(), "Configurazione salvata con successo", Toast.LENGTH_SHORT).show();
                    FragmentHandler.replaceFragment(requireActivity(), new GenericSettingsFragment());
                });
    }
}