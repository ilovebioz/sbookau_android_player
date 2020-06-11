package com.sectic.sbookau.adapter;

/**
 * Created by bioz on 9/20/2014.
 */

import android.os.Handler;

import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.sectic.sbookau.R;
import com.sectic.sbookau.icallback.OnLoadMoreListener;
import com.sectic.sbookau.icallback.OnItemClickListener;
import com.sectic.sbookau.model.Book;
import com.sectic.sbookau.ultils.AppProvider;

import java.util.ArrayList;
import java.util.List;

public class BookAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> {

    private final int VIEW_ITEM_BASE = 1;
    private final int VIEW_PROG = 0;
    private final List<Book> lData;

    private OnItemClickListener mItemClickListener;
    private OnLoadMoreListener onLoadMoreListener;
    private LinearLayoutManager mLinearLayoutManager;

    private boolean bIsMoreLoading = false;

    private int iSelectedIdx = -1;

    public BookAdapter() {
        lData = new ArrayList<>();
        this.setHasStableIds(true);
    }

    //// override functions
    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup viewGroup, int viewType) {
        if (viewType == VIEW_ITEM_BASE) {
            View v = LayoutInflater.from(viewGroup.getContext()).inflate(R.layout.fragment_list_book_item, viewGroup, false);
            return new BookViewHolder(v);
        } else{
            View loadMoreView = LayoutInflater.from(viewGroup.getContext()).inflate(R.layout.progress_item, viewGroup, false);
            return new ProgressViewHolder(loadMoreView);
        }
    }
    @Override
    public void onBindViewHolder(RecyclerView.ViewHolder holder, int position) {
        if (holder instanceof BookViewHolder) {
            holder.itemView.setSelected(iSelectedIdx == position);
            ((BookViewHolder) holder).bindData(lData.get(position), position + 1);
        }
    }
    @Override
    public void onAttachedToRecyclerView(RecyclerView recyclerView) {
        super.onAttachedToRecyclerView(recyclerView);
    }
    @Override
    public int getItemCount() {
        return this.lData.size();
    }
    @Override
    public long getItemId(int position) {
        if(lData.size() <= position || lData.get(position) == null){
            return position;
        }
        return lData.get(position).sId.hashCode();
    }
    @Override
    public int getItemViewType(int position) {
        return (lData.get(position) != null) ? VIEW_ITEM_BASE : VIEW_PROG;
    }

    //// helps
    public void setOnItemClickListener(final OnItemClickListener mItemClickListener) {
        this.mItemClickListener = mItemClickListener;
    }
    public void setLoadMoreListener(OnLoadMoreListener loadMoreListener) {
        this.onLoadMoreListener = loadMoreListener;
    }

    public void setLinearLayoutManager(LinearLayoutManager linearLayoutManager){
        this.mLinearLayoutManager=linearLayoutManager;
    }

    public Book getSelectedItem(){
        if(iSelectedIdx >= 0 && lData.size() > 0){
            return lData.get(iSelectedIdx);
        }else{
            return null;
        }
    }

    public List<Book> getDataList(){
        return lData;
    }

    public void addAll(List<Book> list) {
        lData.addAll(list);
        notifyItemRangeChanged(0, lData.size());
    }

    public void resetAll(List<Book> list) {
        lData.clear();
        lData.addAll(list);
        notifyDataSetChanged();
    }

    public void add(int location, Book iOItem){
        lData.add(location, iOItem);
        notifyItemInserted(location);
    }

    public void move(int from, int to) {
        Book prev = lData.remove(from);
        lData.add(to > from ? to - 1 : to, prev);
        notifyItemMoved(from, to);
    }

    public void set(int location, Book iOItem){
        lData.set(location, iOItem);
        notifyItemChanged(location);
    }

    public void clear(){
        lData.clear();
        notifyDataSetChanged();
    }

    public void setMoreLoading(boolean isMoreLoading) {
        this.bIsMoreLoading=isMoreLoading;
    }

    public void setProgressMore(final boolean isProgress) {
        if (isProgress) {
            new Handler().post(new Runnable() {
                @Override
                public void run() {
                    lData.add(null);
                    notifyItemInserted(lData.size() - 1);
                }
            });
        } else {
            lData.remove(lData.size() - 1);
            notifyItemRemoved(lData.size());
        }
    }

    public void setRecyclerView(RecyclerView mView){
        mView.addOnScrollListener(new RecyclerView.OnScrollListener() {
            @Override
            public void onScrolled(RecyclerView recyclerView, int dx, int dy) {
                super.onScrolled(recyclerView, dx, dy);
                int iTotalItemCount = recyclerView.getChildCount();
                int iVisibleItemCount = mLinearLayoutManager.getItemCount();
                int iFirstVisibleItem = mLinearLayoutManager.findFirstVisibleItemPosition();

                if (!bIsMoreLoading && (iFirstVisibleItem + iVisibleItemCount) >= iTotalItemCount && (iFirstVisibleItem >= 0)) {
                    if (onLoadMoreListener != null) {
                        onLoadMoreListener.onLoadMore();
                    }
                    bIsMoreLoading = true;
                }
            }
        });
    }

    //// sub Class
    public class BookViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener {
        TextView txt_book_id;
        TextView txt_book_name;
        TextView txt_book_part_no;
        TextView txt_book_author;
        TextView txt_like;
        TextView txt_View;
        ImageView imvRecommented;

        BookViewHolder(View itemView) {
            super(itemView);
            itemView.setOnClickListener(this);
            txt_book_id = itemView.findViewById(R.id.txt_book_list_item_id);
            txt_book_name = itemView.findViewById(R.id.txt_book_list_item_name);
            txt_book_part_no = itemView.findViewById(R.id.txt_book_list_item_part_counter);
            txt_book_author = itemView.findViewById(R.id.txt_book_list_item_author);
            txt_like = itemView.findViewById(R.id.txt_like);
            txt_View = itemView.findViewById(R.id.txt_View);

            imvRecommented = itemView.findViewById(R.id.imvRecommented);
        }
        @Override
        public void onClick(View v) {
            if (mItemClickListener != null) {
                mItemClickListener.onItemClick(v, getLayoutPosition());
                notifyItemChanged(iSelectedIdx);
                iSelectedIdx = getLayoutPosition();
                notifyItemChanged(iSelectedIdx);
            }
        }

        void bindData(Book book, int iOrder) {
            try {
                txt_book_id.setText(String.valueOf(iOrder));
                txt_book_name.setText(book.sName);
                txt_book_part_no.setText(String.valueOf(book.iPartCount));

                txt_book_author.setText(book.sAuthor);

                txt_like.setText(String.valueOf(book.iLikeCount));
                txt_View.setText(String.valueOf(book.iViewCount));

                if(book.bIsRecommend){
                    imvRecommented.setColorFilter(AppProvider.getContext().getResources().getColor(R.color.color_res_certificate_icon_true_bg) );
                }else{
                    imvRecommented.setColorFilter(AppProvider.getContext().getResources().getColor(R.color.color_res_certificate_icon_false_bg));
                }
            }catch (Exception e){
                e.printStackTrace();
            }
        }
    }
}