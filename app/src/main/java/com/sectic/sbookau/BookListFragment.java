package com.sectic.sbookau;

import android.content.Intent;
import android.os.Bundle;
import android.app.Fragment;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.recyclerview.widget.SimpleItemAnimator;
import es.dmoral.toasty.Toasty;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.Toast;

import com.sectic.sbookau.adapter.BookAdapter;
import com.sectic.sbookau.ultils.AuthSetting;
import com.sectic.sbookau.icallback.OnLoadMoreListener;
import com.sectic.sbookau.model.Book;
import com.sectic.sbookau.model.BookList;
import com.sectic.sbookau.service.GetBookFromCloudService;
import com.sectic.sbookau.service.GetBookHistoryFromFileService;
import com.sectic.sbookau.icallback.OnItemClickListener;

public class BookListFragment extends Fragment {
    public static final String TAG = BookListFragment.class.getSimpleName();

    private static final String[] PARAMS = {"p1", "p2", "p3"};
    private View mView;
    private String mTag;
    private String mQuery;
    private boolean bFromFile;

    private BookList mBookList;
    private SwipeRefreshLayout mSwipeRefreshLayout;
    private BookAdapter mAdapter;

    public BookListFragment() {}

    public static BookListFragment newInstance(String param1, String param2, String param3) {
        BookListFragment fraLstBook  = new BookListFragment();
        Bundle args = new Bundle();
        args.putString(PARAMS[0], param1);
        args.putString(PARAMS[1], param2);
        args.putString(PARAMS[2], param3);
        fraLstBook.setArguments(args);
        return fraLstBook;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            mTag = getArguments().getString(PARAMS[0]);
            mQuery = getArguments().getString(PARAMS[1]);
            bFromFile = getArguments().getString(PARAMS[2]).equals("FILE");
        }

        if(BuildConfig.enableDebugLogging) {
            Log.i(TAG, "onCreate");
        }
    }

    @Override
    public void onStart() {
        super.onStart();
        fetchDataAsync(1);
        if(BuildConfig.enableDebugLogging) {
            Log.i(TAG, "onStart");
        }
    }

    @Override
    public void onResume() {
        super.onResume();
        if(BuildConfig.enableDebugLogging) {
            Log.i(TAG, "onResume");
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        super.onCreateView(inflater, container, savedInstanceState);
        mView = inflater.inflate(R.layout.fragment_list_book, container, false);
        initViews();
        if(BuildConfig.enableDebugLogging) {
            Log.i(TAG, "onCreateView");
        }
        return mView;
    }

    private void initViews()
    {
        mAdapter = new BookAdapter();
        RecyclerView recyclerView = (RecyclerView) mView.findViewById(R.id.rvBookList);

        ((SimpleItemAnimator) recyclerView.getItemAnimator()).setSupportsChangeAnimations(false);

        LinearLayoutManager mLayoutManager = new LinearLayoutManager(this.getActivity());

        recyclerView.setLayoutManager(mLayoutManager);
        mAdapter.setLinearLayoutManager(mLayoutManager);
        mAdapter.setRecyclerView(recyclerView);
        recyclerView.setAdapter(mAdapter);

        mAdapter.setLoadMoreListener(new OnLoadMoreListener() {
            @Override
            public void onLoadMore() {
                fetchDataAsync(0);
            }
        });

        mAdapter.setOnItemClickListener(new OnItemClickListener() {
            @Override
            public void onItemClick(View view, int position) {
                try {
                    Book oBook = mAdapter.getDataList().get(position);
                    new BookList().updateBookHistoryFromInternalStorage(getActivity(), oBook);

                    Intent intent = new Intent("com.sectic.sbookau.PLAYINGLIST");
                    intent.putExtra("name", oBook.sName);
                    intent.putExtra("id", oBook.sId);
                    intent.putExtra("bLoadPref", 0);

                    startActivity(intent);
                }catch(Exception e){
                    e.printStackTrace();
                }
            }
        });

        // search book
        ImageButton btnSearch = (ImageButton) mView.findViewById(R.id.btn_search_book);
        btnSearch.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                EditText txtSearchValue = (EditText) mView.findViewById(R.id.txt_search_value);
                MainActivity oMainAct = (MainActivity)getActivity();
                if( txtSearchValue.getText().toString().trim().length() > 0 ) {
                    oMainAct.sTextQuery = txtSearchValue.getText().toString();
                    oMainAct.displayView(-1);
                }else{
                    Toasty.warning(oMainAct.getApplicationContext(), getString(R.string.s_res_mess_search_content_empty), Toast.LENGTH_SHORT, true).show();
                }
            }
        });

        mSwipeRefreshLayout = (SwipeRefreshLayout) mView.findViewById(R.id.srlBookList);
        mSwipeRefreshLayout.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener(){
            @Override
            public void onRefresh(){
                fetchDataAsync(1);
            }
        });

        if(BuildConfig.enableDebugLogging) {
            Log.i(TAG, "initViews");
        }
    }

    private void fetchDataAsync(int iIPage){
        if(iIPage == 0) {
            if(mBookList != null && mBookList.isHasNext()){
                iIPage = mBookList.oPages.iNext;
            }else {
                return;
            }
        }

        mAdapter.setProgressMore(true);

        if(!bFromFile){
            final MainActivity oMainAct = (MainActivity)getActivity();
            final GetBookFromCloudService oGetBookFromCloudService = new GetBookFromCloudService(AuthSetting.gToken, mTag, mQuery, iIPage);
            oGetBookFromCloudService.oITaskCompleted = new GetBookFromCloudService.OnTaskCompleted() {
                @Override
                public void onTaskCompleted(boolean bResult, BookList oResponseBookList) {
                    mAdapter.setProgressMore(false);
                    if(bResult){
                        mBookList = oResponseBookList;
                        if (mBookList.isHasPre()) {
                            mAdapter.addAll(mBookList.lOBook);
                        } else {
                            mAdapter.resetAll(mBookList.lOBook);
                        }
                    }else{
                        Toasty.error(oMainAct,getString(R.string.s_res_mess_can_not_get_book_from_server),Toast.LENGTH_SHORT, true).show();
                    }
                    mAdapter.setMoreLoading(false);
                    mSwipeRefreshLayout.setRefreshing(false);

                    if(mBookList != null && mBookList.lOBook.size() > 0) {
                        mView.findViewById(R.id.img_book_empty).setVisibility(View.INVISIBLE);
                    }else {
                        mView.findViewById(R.id.img_book_empty).setVisibility(View.VISIBLE);
                    }
                }
            };
            oGetBookFromCloudService.execute();
        }else{
            final MainActivity oMainAct = (MainActivity)getActivity();
            final GetBookHistoryFromFileService oGetBookHistoryFromFileService = new GetBookHistoryFromFileService();
            oGetBookHistoryFromFileService.oITaskCompleted = new GetBookHistoryFromFileService.OnTaskCompleted() {
                @Override
                public void onTaskCompleted(boolean bResult, BookList oResponseBookList) {
                    mAdapter.setProgressMore(false);
                    if(bResult){
                        if(mBookList == null){
                            mBookList = new BookList();
                        }
                        oGetBookHistoryFromFileService.parseBookDataFromFileResult(mBookList.lOBook);
                        mAdapter.resetAll(mBookList.lOBook);
                    }else{
                        Toasty.error(oMainAct,getString(R.string.s_res_mess_load_book_from_file_fail),Toast.LENGTH_SHORT, true).show();
                    }
                    mAdapter.setMoreLoading(false);
                    mSwipeRefreshLayout.setRefreshing(false);

                    if(mBookList != null && mBookList.lOBook.size() > 0) {
                        mView.findViewById(R.id.img_book_empty).setVisibility(View.INVISIBLE);
                    }else {
                        mView.findViewById(R.id.img_book_empty).setVisibility(View.VISIBLE);
                    }
                }
            };
            oGetBookHistoryFromFileService.execute();
        }
        if(BuildConfig.enableDebugLogging) {
            Log.i(TAG, "fetchDataAsync");
        }
    }
}
