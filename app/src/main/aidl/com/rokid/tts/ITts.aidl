package com.rokid.tts;

import com.rokid.tts.ITtsCallback;

interface ITts {
	int speak(String content, ITtsCallback cb);

	void stop(int id);
}