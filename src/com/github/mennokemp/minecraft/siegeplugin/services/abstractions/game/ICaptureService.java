package com.github.mennokemp.minecraft.siegeplugin.services.abstractions.game;

import com.github.mennokemp.minecraft.pluginhelpers.Result;
import com.github.mennokemp.minecraft.siegeplugin.domain.game.CapturePoint;

public interface ICaptureService extends IGameEventListener
{
	Result startCapture(CapturePoint capturePoint);
	
	Result stopCapture(CapturePoint capturePoint);
}
