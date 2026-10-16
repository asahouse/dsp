package org.codeworks.dsp.controller.site.creatives;

import com.qiniu.util.Auth;
import com.qiniu.util.StringMap;
import org.codeworks.dsp.model.dto.Response;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiParam;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.Set;

/**
 * 七牛云对象存储创意上传凭证接口
 * <p>
 * 提供向七牛云安全上传图片/创意所需的 Upload Token 签发服务，绑定广告主专属保存路径。
 * </p>
 *
 * @author Luis
 */
@Api(tags = "七牛云存储凭证接口", description = "提供创意物料直传七牛云对象存储所需的授权 Token 签发")
@RequestMapping("/v2/creatives/upload")
@RestController("/v2/creatives/QiniuController")
public class QiniuController {

    @Autowired
    private StringMap imagePolicyMap;

    @Autowired
    private Auth qiniuAuth;

    @Value("${qiniu.bucket}")
    private String bucketName;

    @Value("#{'${upload.image.size}'.split(',')}")
    private Set<String> restrictImageSizes;

    /**
     * 获取七牛云上传授权凭证 Token
     *
     * @param advId 广告主ID
     * @return 包含上传 Token 的响应
     */
    @ApiOperation(value = "获取七牛上传Token", notes = "签发具有有效期的七牛云上传凭证，限定保存文件路径至当前广告主专属目录")
    @GetMapping(path = "/token")
    public Response uploadToken(@ApiParam(value = "广告主ID", required = true) @RequestParam Long advId) {
        StringMap sm = new StringMap(imagePolicyMap.map());
        sm.put("saveKey", String.format("creatives/%d/$(etag)$(ext)", advId));

        return Response.ok("token", getToken(sm));
    }

    private String getToken(StringMap policyMap) {
        return qiniuAuth.uploadToken(bucketName, null, 3600l, policyMap, true);
    }
}
