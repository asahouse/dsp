package org.codeworks.dsp.controller.site.creatives;

import com.qiniu.util.Auth;
import com.qiniu.util.StringMap;
import org.codeworks.dsp.model.dto.Response;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.Set;

/**
 * 七牛上传接口
 * Created by Luis on 2016/9/14.
 */
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

    @GetMapping(path = "/token")
    public Response uploadToken(@RequestParam Long advId) {
        StringMap sm = new StringMap(imagePolicyMap.map());
        sm.put("saveKey", String.format("creatives/%d/$(etag)$(ext)", advId));

        return Response.ok("token", getToken(sm));
    }

    private String getToken(StringMap policyMap) {
        return qiniuAuth.uploadToken(bucketName, null, 3600l, policyMap, true);
    }
}
