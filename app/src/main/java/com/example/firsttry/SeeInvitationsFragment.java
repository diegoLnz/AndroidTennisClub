package com.example.firsttry;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.firsttry.businesslogic.CourtsBookBl;
import com.example.firsttry.businesslogic.LessonsBl;
import com.example.firsttry.enums.CourtBookRequestStatus;
import com.example.firsttry.extensions.ValidatedFragment;
import com.example.firsttry.extensions.adapters.BookRequestAdapter;
import com.example.firsttry.extensions.adapters.BookedCourtAdapter;
import com.example.firsttry.extensions.adapters.SearchedLessonAdapter;
import com.example.firsttry.models.CourtBookRequest;
import com.example.firsttry.models.Lesson;
import com.example.firsttry.utilities.AccountManager;
import com.example.firsttry.utilities.Array;

import java.util.Objects;
import java.util.concurrent.CompletableFuture;

public class SeeInvitationsFragment
        extends ValidatedFragment
        implements BookRequestAdapter.OnUserActionListener
{
    private RecyclerView recyclerView;
    private BookRequestAdapter adapter;

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState)
    {
        currentView = inflater.inflate(R.layout.fragment_see_invitations, container, false);
        updateRecyclerView();
        return currentView;
    }

    private void updateRecyclerView()
    {
        getUserLessons().thenAccept(this::setRecyclerView);
    }

    private void setRecyclerView(Array<CourtBookRequest> requests)
    {
        recyclerView = currentView.findViewById(R.id.requestsRecycleView);
        recyclerView.setLayoutManager(new LinearLayoutManager(requireActivity()));

        adapter = new BookRequestAdapter(requests, this);
        recyclerView.setAdapter(adapter);
    }

    private CompletableFuture<Array<CourtBookRequest>> getUserLessons()
    {
        return Objects.requireNonNull(AccountManager.getCurrentAccount())
                .thenCompose(CourtsBookBl::getRequestsByTargetUser);
    }

    @Override
    public void onAccept(CourtBookRequest request) {
        request.setStatus(CourtBookRequestStatus.Accepted);
        request.save()
                .thenAccept(res -> {
                    request.updateCourtBook();
                    Toast.makeText(requireActivity(), "Invito accettato", Toast.LENGTH_SHORT).show();
                    updateRecyclerView();
                });
    }

    @Override
    public void onDeny(CourtBookRequest request) {
        request.setStatus(CourtBookRequestStatus.NotAccepted);
        request.save()
                .thenAccept(res -> {
                    Toast.makeText(requireActivity(), "Invito rifiutato", Toast.LENGTH_SHORT).show();
                    updateRecyclerView();
                });
    }
}
