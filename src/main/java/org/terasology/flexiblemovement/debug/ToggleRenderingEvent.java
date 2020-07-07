// Copyright 2020 The Terasology Foundation
// SPDX-License-Identifier: Apache-2.0

package org.terasology.flexiblemovement.debug;

import org.terasology.input.BindButtonEvent;
import org.terasology.input.DefaultBinding;
import org.terasology.input.InputType;
import org.terasology.input.Keyboard;
import org.terasology.input.RegisterBindButton;

@RegisterBindButton(id = "togglerendering", category = "flexiblemovementtestbed", description = "Toggle Path Debug Rendering")
@DefaultBinding(type = InputType.KEY, id = Keyboard.KeyId.F6)
public class ToggleRenderingEvent extends BindButtonEvent {
}
