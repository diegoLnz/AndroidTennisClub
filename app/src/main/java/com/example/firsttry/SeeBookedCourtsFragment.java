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
import com.example.firsttry.extensions.adapters.BookedCourtAdapter;
import com.example.firsttry.models.CourtBook;
import com.example.firsttry.models.CourtBookRequest;
import com.example.firsttry.models.Lesson;
import com.example.firsttry.utilities.AccountManager;
import com.example.firsttry.utilities.Array;

import java.util.List;
import java.util.Objects;
import java.util.concurrent.CompletableFuture;

public class SeeBookedCourtsFragment
        extends ValidatedFragment
        implements BookedCourtAdapter.OnUserActionListener
{
    private RecyclerView recyclerView;
    private BookedCourtAdapter adapter;

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState)
    {
        currentView = inflater.inflate(R.layout.fragment_see_booked_courts, container, false);
        AccountManager.getCurrentAccount().thenAccept(account -> {
            CurrentUser = account;
            updateRecyclerView();
        });
        return currentView;
    }

    private void updateRecyclerView()
    {
        getUserCourtBooks().thenAccept(this::setRecyclerView);
    }

    private void setRecyclerView(Array<CourtBook> courtBooks)
    {
        recyclerView = currentView.findViewById(R.id.availableCourtsRecycleView);
        recyclerView.setLayoutManager(new LinearLayoutManager(requireActivity()));

        adapter = new BookedCourtAdapter(courtBooks, this);
        recyclerView.setAdapter(adapter);
    }

    private CompletableFuture<Array<CourtBook>> getUserCourtBooks()
    {
        return Objects.requireNonNull(AccountManager.getCurrentAccount())
                .thenCompose(CourtsBookBl::getCourtBooksByUser);
    }

    @Override
    public void onDelete(CourtBook courtBook)
    {
        CourtsBookBl.deleteCourtBookAndItsRequests(courtBook).thenAccept(res -> {
            Toast.makeText(requireActivity(), "Prenotazione annullata!", Toast.LENGTH_SHORT).show();
            updateRecyclerView();
        });
    }

    @Override
    public void onInvitationCancel(CourtBook courtBook)
    {
        CourtBookRequest.list(req -> req.getCourtBookId().equals(courtBook.getId())
                        && req.getTargetUserId().equals(CurrentUser.getId()))
                .thenAccept(requests ->
                {
                    CourtBookRequest request = requests.firstOrDefault();
                    request.setStatus(CourtBookRequestStatus.NotAccepted);
                    List<String> ids = courtBook.getUserIds();
                    ids.forEach(id -> {
                        if(id.equals(CurrentUser.getId()))
                        {
                            ids.remove(id);
                        }
                    });
                    courtBook.setUserIds(ids);
                    request.save();
                    courtBook.updateRequestsStatusWhenOneUserCancelsInvitation();
                    courtBook.safeSave();
                    updateRecyclerView();
                });
    }


}
