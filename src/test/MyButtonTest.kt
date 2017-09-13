import com.iezview.server.controls.toolbarbutton.Prem
import com.iezview.server.controls.toolbarbutton.segmentedbutton
import com.iezview.server.controls.toolbarbutton.toolbarbutton
import javafx.scene.Parent
import tornadofx.*

class SegmentedButtonTest: View(){
    override val root=hbox {

        toolbar {
            hbox{
                togglegroup{
                    togglebutton {  }
                    togglebutton {  }
                    togglebutton {  }
                    togglebutton {  }
                }
            }

        }
    }


}