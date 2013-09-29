package org.microg.playstore.fragments;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import com.android.vending.R;
import com.google.play.DfeResponse;
import com.google.play.proto.Documents;
import com.squareup.picasso.Picasso;
import org.microg.playstore.StoreApplication;
import org.microg.playstore.utils.EasyThread;

public class DocDetailsFragment extends Fragment {

	private Documents.DocV2 doc;
	private EasyThread<DfeResponse<Documents.DetailsResponse>> waiting;
	private View view;
	private static final String TAG = "StoreDetailsFragment";

	public DocDetailsFragment(final String id) {
		waiting = EasyThread.start(new EasyThread.ResultRunnable<DfeResponse<Documents.DetailsResponse>>() {
			@Override
			public DfeResponse<Documents.DetailsResponse> run() {
				Log.d("StoreTest", "start");
				StoreApplication.getInstance().refreshContext();
				return StoreApplication.getInstance().getDfeClient().requestDetails(id);
			}
		});
	}

	private void drawDoc() {
		((TextView) view.findViewById(R.id.doc_title)).setText(doc.getTitle());
		((TextView) view.findViewById(R.id.doc_author)).setText(doc.getCreator().toUpperCase());
		if (doc.getImageCount() > 0) {
			new Picasso.Builder(getActivity()).debugging(true).build().load(doc.getImage(0).getImageUrl())
											  .into((ImageView) view.findViewById(R.id.doc_icon));
		}
	}

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
		view = inflater.inflate(R.layout.docdetails, null);
		waiting.onResult(new EasyThread.ResultCallback<DfeResponse<Documents.DetailsResponse>>() {
			@Override
			public void done(DfeResponse<Documents.DetailsResponse> result) {
				if (result.hasError()) {
					thrown(new Exception(result.getStatusCode() + " " + result.getStatusString(),
										 result.getThrowable()));
					return;
				}
				doc = result.getResponse().getDocV2();
				getActivity().runOnUiThread(new Runnable() {
					@Override
					public void run() {
						drawDoc();
					}
				});
			}

			@Override
			public void thrown(Throwable t) {
				Log.w("StoreTest", t);
				// TODO: show error
			}
		});
		return view;
	}
}
