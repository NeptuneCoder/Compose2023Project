/*
 * Videotoolbox hardware acceleration
 *
 * copyright (c) 2012 Sebastien Zwickert
 *
 * This file is part of FFmpeg.
 *
 * FFmpeg is free software; you can redistribute it and/or
 * modify it under the terms of the GNU Lesser General Public
 * License as published by the Free Software Foundation; either
 * version 2.1 of the License, or (at your option) any later version.
 *
 * FFmpeg is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the GNU
 * Lesser General Public License for more details.
 *
 * You should have received a copy of the GNU Lesser General Public
 * License along with FFmpeg; if not, write to the Free Software
 * Foundation, Inc., 51 Franklin Street, Fifth Floor, Boston, MA 02110-1301 USA
 */

#ifndef AVCODEC_VIDEOTOOLBOX_H
#define AVCODEC_VIDEOTOOLBOX_H

/**
 * @file
 * @ingroup lavc_codec_hwaccel_videotoolbox
 * Public libavcodec Videotoolbox header.
 */

/**
 * @defgroup lavc_codec_hwaccel_videotoolbox VideoToolbox Decoder
 * @ingroup lavc_codec_hwaccel
 *
 * Hardware accelerated decoding using VideoToolbox on Apple Platforms
 *
 * @{
 */

#include <stdint.h>

#define Picture QuickdrawPicture
#include <VideoToolbox/VideoToolbox.h>
#undef Picture

#include "libavcodec/avcodec.h"

#include "libavutil/attributes.h"

/**
 * This struct holds all the information that needs to be passed
 * between the caller and libavcodec for initializing Videotoolbox decoding.
 * Its size is not a part of the public ABI, it must be allocated with
 * av_videotoolbox_alloc_context() and freed with av_free().
 */
typedef struct AVVideotoolboxContext {
    /**
     * Videotoolbox decompression session object.
     */
    VTDecompressionSessionRef session;

#if FF_API_VT_OUTPUT_CALLBACK
    /**
     * The output videoCallback that must be passed to the session.
     * Set by av_videottoolbox_default_init()
     */
    attribute_deprecated
    VTDecompressionOutputCallback output_callback;
#endif

    /**
     * CVPixelBuffer Format Type that Videotoolbox will use for decoded frames.
     * set by the caller. If this is set to 0, then no specific format is
     * requested from the decoder, and its native format is output.
     */
    OSType cv_pix_fmt_type;

    /**
     * CoreMedia Format Description that Videotoolbox will use to create the decompression session.
     */
    CMVideoFormatDescriptionRef cm_fmt_desc;

    /**
     * CoreMedia codec type that Videotoolbox will use to create the decompression session.
     */
    int cm_codec_type;
} AVVideotoolboxContext;

#if FF_API_VT_HWACCEL_CONTEXT

/**
 * Allocate and initialize a Videotoolbox codecContext.
 *
 * This function should be called from the get_format() videoCallback when the caller
 * selects the AV_PIX_FMT_VIDETOOLBOX format. The caller must then create
 * the decoder object (using the output videoCallback provided by libavcodec) that
 * will be used for Videotoolbox-accelerated decoding.
 *
 * When decoding with Videotoolbox is finished, the caller must destroy the decoder
 * object and free the Videotoolbox codecContext using av_free().
 *
 * @return the newly allocated codecContext or NULL on failure
 * @deprecated Use AVCodecContext.hw_frames_ctx or hw_device_ctx instead.
 */
attribute_deprecated
AVVideotoolboxContext *av_videotoolbox_alloc_context(void);

/**
 * This is a convenience function that creates and sets up the Videotoolbox codecContext using
 * an internal implementation.
 *
 * @param avctx the corresponding codec codecContext
 *
 * @return >= 0 on success, a negative AVERROR code on failure
 * @deprecated Use AVCodecContext.hw_frames_ctx or hw_device_ctx instead.
 */
attribute_deprecated
int av_videotoolbox_default_init(AVCodecContext *avctx);

/**
 * This is a convenience function that creates and sets up the Videotoolbox codecContext using
 * an internal implementation.
 *
 * @param avctx the corresponding codec codecContext
 * @param vtctx the Videotoolbox codecContext to use
 *
 * @return >= 0 on success, a negative AVERROR code on failure
 * @deprecated Use AVCodecContext.hw_frames_ctx or hw_device_ctx instead.
 */
attribute_deprecated
int av_videotoolbox_default_init2(AVCodecContext *avctx, AVVideotoolboxContext *vtctx);

/**
 * This function must be called to free the Videotoolbox codecContext initialized with
 * av_videotoolbox_default_init().
 *
 * @param avctx the corresponding codec codecContext
 * @deprecated Use AVCodecContext.hw_frames_ctx or hw_device_ctx instead.
 */
attribute_deprecated
void av_videotoolbox_default_free(AVCodecContext *avctx);

#endif /* FF_API_VT_HWACCEL_CONTEXT */

/**
 * @}
 */

#endif /* AVCODEC_VIDEOTOOLBOX_H */
