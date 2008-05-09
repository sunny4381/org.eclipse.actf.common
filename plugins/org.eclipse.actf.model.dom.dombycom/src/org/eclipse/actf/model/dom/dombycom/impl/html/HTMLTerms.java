/*******************************************************************************
 * Copyright (c) 2007 IBM Corporation and Others
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *    Daisuke SATO - initial API and implementation
 *******************************************************************************/

package org.eclipse.actf.model.dom.dombycom.impl.html;

import java.util.HashSet;

import org.eclipse.actf.model.dom.dombycom.INodeEx;
import org.eclipse.actf.model.dom.dombycom.impl.Helper;
import org.eclipse.actf.model.dom.dombycom.impl.html.ElementImpl.InputType;
import org.eclipse.actf.util.vocab.AbstractTerms;
import org.eclipse.actf.util.vocab.IEvalTarget;
import org.eclipse.actf.util.vocab.Vocabulary;
import org.eclipse.actf.util.win32.comclutch.ComPlugin;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;
import org.w3c.dom.Text;




public class HTMLTerms extends AbstractTerms {
    // ADDED BY KOBAYASHI ( ////////////////////////////////
    //
    @Override
    public boolean isAlterable(IEvalTarget target) {
        return isImage(target);
    }

    private static HTMLTerms instance;

    private static HashSet<String> headingTags;

    private static HashSet<String> listItemTags;

    private static HashSet<String> blockTags;

    private static HashSet<String> listTopTags;

    static {
        headingTags = new HashSet<String>();
        headingTags.add("H1".intern());
        headingTags.add("H2".intern());
        headingTags.add("H3".intern());
        headingTags.add("H4".intern());
        headingTags.add("H5".intern());
        headingTags.add("H6".intern());

        listItemTags = new HashSet<String>();
        listItemTags.add("LI".intern());
        listItemTags.add("DT".intern());

        listTopTags = new HashSet<String>();
        listTopTags.add("UL".intern());
        listTopTags.add("OL".intern());
        listTopTags.add("DL".intern());

        blockTags = new HashSet<String>();
        blockTags.add("H1".intern());
        blockTags.add("H2".intern());
        blockTags.add("H3".intern());
        blockTags.add("H4".intern());
        blockTags.add("H5".intern());
        blockTags.add("H6".intern());
        blockTags.add("B".intern());
        blockTags.add("STRONG".intern());
        blockTags.add("DIV".intern());
        blockTags.add("SPAN".intern());
        blockTags.add("P".intern());
        blockTags.add("FONT".intern());

    }

    public static HTMLTerms getInstance() {
        if (instance == null) {
            instance = new HTMLTerms();
        }
        return instance;
    }

    @Override
    public boolean isValidNode(IEvalTarget target) {
        if (target instanceof ElementImpl) {
            ElementImpl element = (ElementImpl) target;
            String name = element.getLocalName();
            if ("HEAD".equals(name) || "MAP".equals(name)) 
                return false;
        }
        return true;
    }

    @Override
    public boolean isVisibleNode(IEvalTarget target) {
        return true;
    }

    @Override
    public boolean isMultilineEdit(IEvalTarget target) {
        if (target instanceof ElementImpl) {
            ElementImpl element = (ElementImpl) target;
            String name = element.getLocalName();
            if ("TEXTAREA".equals(name)) {
                return true;
            }
        } else if (target instanceof TextImpl) {
            TextImpl text = (TextImpl) target;
            Node parent = text.getParentNode();
            if (parent instanceof IEvalTarget) {
                return isMultilineEdit((IEvalTarget) parent);
            }
        }
        return false;
    }

    @Override
    public boolean isSelectOption(IEvalTarget target) {
        if (target instanceof ElementImpl) {
            ElementImpl element = (ElementImpl) target;
            String name = element.getLocalName();
            if ("OPTION".equals(name)) {
                return true;
            }
        } else if (target instanceof TextImpl) {
            TextImpl text = (TextImpl) target;
            Node parent = text.getParentNode();
            if (parent instanceof IEvalTarget) {
                return isSelectOption((IEvalTarget) parent);
            }
        }
        return false;
    }

    @Override
    public boolean isInputable(IEvalTarget target) {
        if (target instanceof ElementImpl) {
            ElementImpl element = (ElementImpl) target;
            String name = element.getLocalName();
            if ("INPUT".equals(name)) {
                InputType type = element.getInputType();
                if (type == null)
                    return false;
                return type.isEdit() || type.isPassword() || type.isFileEdit() || type.isCheckBox() || type.isRadio();
            } else if ("TEXTAREA".equals(name)) {
                return true;
            }
        } else if (target instanceof TextImpl) {
            TextImpl text = (TextImpl) target;
            Node parent = text.getParentNode();
            if (parent instanceof IEvalTarget) {
                return Vocabulary.isInputable().eval((IEvalTarget) parent);
            }
        }

        return false;
    }

    @Override
    public boolean isCombobox(IEvalTarget target) {
        if (target instanceof ElementImpl) {
            ElementImpl element = (ElementImpl) target;
            String name = element.getLocalName();
            if ("SELECT".equals(name)) {
                return true;
            }
        } else if (target instanceof TextImpl) {
            TextImpl text = (TextImpl) target;
            Node parent = text.getParentNode();
            if (parent instanceof IEvalTarget) {
                return Vocabulary.isSelectable().eval((IEvalTarget) parent);
            }
        }

        return false;
    }

    @Override
    public boolean isMultiSelectable(IEvalTarget target) {
        if (target instanceof ElementImpl) {
            ElementImpl element = (ElementImpl) target;
            String name = element.getLocalName();
            if ("SELECT".equals(name)) {
                return (Boolean) Helper.get(element.getINode(), "multiple");
            }
        } else if (target instanceof TextImpl) {
            TextImpl text = (TextImpl) target;
            Node parent = text.getParentNode();
            if (parent instanceof IEvalTarget) {
                return Vocabulary.isMultiSelectable().eval((IEvalTarget) parent);
            }
        }

        return false;
    }

    @Override
    public boolean isClickable(IEvalTarget target) {
        if (target instanceof TextImpl) {
            TextImpl text = (TextImpl) target;
            Node parent = text.getParentNode();
            if (parent instanceof IEvalTarget) {
                return Vocabulary.isClickable().eval((IEvalTarget) parent);
            }
        }
        if (target instanceof ElementImpl) {
            ElementImpl element = (ElementImpl) target;
            String name = element.getLocalName();
            String href = (String) Helper.get(element.getINode(), "href");
            if (("A".equals(name) || "AREA".equals(name)) 
                    && href != null && href.length() > 0)
                return true;
            if ("BUTTON".equals(name) || "OPTION".equals(name))
                return true;
            if ("INPUT".equals(name)) {
                InputType type = element.getInputType();
                if (type != null) {
                    return type.isClickable();
                }
            }

            if (Helper.hasProperty(element.getINode(), "onclick"))
                return true;

        }
        if (target instanceof Node) {
            Node node = (Node) target;
            Node p = node.getParentNode();
            if (p instanceof IEvalTarget)
                return Vocabulary.isClickable().eval((IEvalTarget) p);
        }

        return false;
    }

    @Override
    public boolean isLink(IEvalTarget target) {
        if (target instanceof TextImpl) {
            TextImpl text = (TextImpl) target;
            Node parent = text.getParentNode();
            if (parent instanceof IEvalTarget) {
                return Vocabulary.isLink().eval((IEvalTarget) parent);
            }
        }
        if (target instanceof ElementImpl) {
            ElementImpl element = (ElementImpl) target;
            String name = element.getLocalName();
            String href = (String) Helper.get(element.getINode(), "href");
            if (("A".equals(name) || "AREA".equals(name)) 
                    && href != null && href.length() > 0)
                return true;
            
        }
        if (target instanceof Node) {
            Node node = (Node) target;
            Node p = node.getParentNode();
            if (p instanceof IEvalTarget)
                return Vocabulary.isLink().eval((IEvalTarget) p);
        }
        return false;
    }

    @Override
    public boolean isCheckbox(IEvalTarget target) {
        if (target instanceof ElementImpl) {
            ElementImpl element = (ElementImpl) target;
            InputType type = element.getInputType();
            if (type != null)
                return type.isCheckBox();
        }
        return false;
    }

    @Override
    public boolean isChecked(IEvalTarget target) {
        if (target instanceof ElementImpl) {
            Object obj = Helper.get(((ElementImpl) target).getINode(), "checked");
            if (obj != null)
                return (Boolean) obj;
        }
        return false;
    }

    @Override
    public boolean isRadio(IEvalTarget target) {
        if (target instanceof ElementImpl) {
            ElementImpl element = (ElementImpl) target;
            InputType type = element.getInputType();
            if (type != null)
                return type.isRadio();
        }
        return false;
    }

    @Override
    public boolean isTextbox(IEvalTarget target) {
        if (target instanceof ElementImpl) {
            ElementImpl element = (ElementImpl) target;
            InputType type = element.getInputType();
            if (type != null)
                return type.isEdit();
        }
        return false;
    }

    @Override
    public boolean isPassword(IEvalTarget target) {
        if (target instanceof ElementImpl) {
            ElementImpl element = (ElementImpl) target;
            InputType type = element.getInputType();
            if (type != null)
                return type.isPassword();
        }
        return false;
    }

    @Override
    public boolean isButton(IEvalTarget target) {
        if (target instanceof ElementImpl) {
            ElementImpl element = (ElementImpl) target;
            InputType type = element.getInputType();
            if (type != null)
                return type.isButton();
        }
        return false;
    }

    private boolean isInTags(IEvalTarget target, HashSet<String> tags) {
        if (target instanceof ElementImpl) {
            ElementImpl element = (ElementImpl) target;
            String name = element.getNodeName();
            if (tags.contains(name.intern()))
                return true;
        }

        return false;
    }

    @Override
    public boolean isListTop(IEvalTarget target) {
        if (isInTags(target, listTopTags)) {
            if (target instanceof Element) {
                Element e = (Element) target;
                NodeList nl = e.getChildNodes();
                int count = 0;
                for (int i = 0; i < nl.getLength(); i++) {
                    Node n = nl.item(i);
                    if (n instanceof IEvalTarget && isListItem((IEvalTarget) n))
                        count++;
                }
                if (count > 1)
                    return true;
            }
        }
        return false;
    }

    @Override
    public boolean isListItem(IEvalTarget target) {
        return isListItemJumpPoint(target);
    }

    @Override
    public boolean isFileEdit(IEvalTarget target) {
        if (target instanceof ElementImpl) {
            ElementImpl element = (ElementImpl) target;
            InputType type = element.getInputType();
            if (type != null)
                return type.isFileEdit();
        }
        return false;
    }

    @Override
    public boolean isEmbeddedObject(IEvalTarget target) {
        if (target instanceof ElementImpl) {
            ElementImpl element = (ElementImpl) target;

            String name = element.getLocalName();
            if ("EMBED".equals(name) || "OBJECT".equals(name))
                return true;
        }
        return false;
    }

    @Override
    public boolean isImage(IEvalTarget target) {
        if (!(target instanceof ElementImpl))
            return false;

        ElementImpl element = (ElementImpl) target;
        String name = element.getLocalName();
        return "IMG".equals(name);
    }
    
    @Override
    public boolean hasContent(IEvalTarget target) {
        if (isMultilineEdit(target)) {
            if (target instanceof Element) {
                return !hasContent((IEvalTarget) ((Element) target).getFirstChild());
            }
        }
        if (isInputable(target)) return true;
        if (isListTop(target)) return true;
        return super.hasContent(target);
    }

    private static final HashSet<String> reducibleElementNames = new HashSet<String>();
    static {
        reducibleElementNames.add("FONT".intern());
        reducibleElementNames.add("BASEFONT".intern());
        reducibleElementNames.add("S".intern());
        reducibleElementNames.add("STRIKE".intern());
        reducibleElementNames.add("U".intern());
        reducibleElementNames.add("EM".intern());
        reducibleElementNames.add("STRONG".intern());
        reducibleElementNames.add("SPAN".intern());
        reducibleElementNames.add("VAR".intern());
        reducibleElementNames.add("CODE".intern());
        reducibleElementNames.add("CITE".intern());
        reducibleElementNames.add("ABBR".intern());
        reducibleElementNames.add("ACRONYM".intern());
        reducibleElementNames.add("Q".intern());
        reducibleElementNames.add("B".intern());
        reducibleElementNames.add("BIG".intern());
        reducibleElementNames.add("I".intern());
        reducibleElementNames.add("SMALL".intern());
        reducibleElementNames.add("TT".intern());
        reducibleElementNames.add("SUB".intern());
        reducibleElementNames.add("SUP".intern());
        
        // non official tag
        reducibleElementNames.add("WBR".intern());
    }

    @Override
    public boolean isReducible(IEvalTarget target) {
        // Might it be included in the rules?
        // if (Vocabulary.isClickable().eval(target)) return false;
        if (!(target instanceof ElementImpl))
            return false;
        ElementImpl element = (ElementImpl) target;
        String name = element.getLocalName().intern();
        return reducibleElementNames.contains(name);
    }

    @Override
    public boolean isVisitedLink(IEvalTarget target) {
        if (!(target instanceof INodeEx))
            return false;
        INodeEx nex = (INodeEx) target;
        
        String url = nex.getLinkURI();
        if (url == null)
            return false;
        
        return ComPlugin.getDefault().getBrowserHistory().isVisited(url);
    }

    @Override
    public boolean isHeadingJumpPoint(IEvalTarget target) {
        return isJumpPoint(target, headingTags);
    }

    @Override
    public boolean isListItemJumpPoint(IEvalTarget target) {
        return isJumpPoint(target, listItemTags);
    }

    @Override
    public boolean isBlockJumpPointF(IEvalTarget target) {
        return isJumpPoint(target, blockTags, 3) || isJumpPoint2(target, headingTags, 3);
    }

    @Override
    public boolean isBlockJumpPointB(IEvalTarget target) {
        return isJumpPoint(target, blockTags, 3) || isJumpPoint2(target, headingTags, 3);
    }

    private boolean isJumpPoint(IEvalTarget target, HashSet tags) {
        return isJumpPoint(target, tags, 100);
    }

    private boolean isJumpPoint(IEvalTarget target, HashSet tags, int depth) {
        if (depth <= 0)
            return false;

        if (target instanceof Element) {
            Element element = (Element) target;
            String name = element.getLocalName();
            if (tags.contains(name.intern())) {
                return true;
            }
        }

        if (!(target instanceof Node))
            return false;
        Node node = (Node) target;
        Node prev = node.getPreviousSibling();
        while (prev != null) {
            if (hasText(prev)) {
                return false;
            }
            prev = prev.getPreviousSibling();
        }

        Node parent = node.getParentNode();
        if (parent == null)
            return false;

        if (parent instanceof IEvalTarget) {
            return isJumpPoint((IEvalTarget) parent, tags, depth - 1);
        }

        return false;
    }

    private boolean isJumpPoint2(IEvalTarget target, HashSet tags, int depth) {
        if (depth <= 0)
            return false;

        if (target instanceof Element) {
            Element element = (Element) target;
            String name = element.getLocalName();
            if (tags.contains(name.intern())) {
                return true;
            }
        }

        if (!(target instanceof Node))
            return false;
        Node node = (Node) target;

        Node parent = node.getParentNode();
        if (parent == null)
            return false;

        if (parent instanceof IEvalTarget) {
            return isJumpPoint((IEvalTarget) parent, tags, depth - 1);
        }

        return false;
    }

    private boolean hasText(Node prev) {
        if (prev instanceof INodeEx) {
            INodeEx nex = (INodeEx) prev;
            if (nex.hasChildNodes()) {
                NodeList list = nex.getChildNodes();
                for (int i = 0; i < list.getLength(); i++) {
                    Node cn = list.item(i);
                    if (hasText(cn))
                        return true;
                }
            } else {
                if (nex.extractString().length() > 0) {
                    return true;
                }
            }
        }
        return false;
    }

    @Override
    public boolean isAccessKey(char key, IEvalTarget node) {
        if (!(node instanceof INodeEx))
            return false;
        INodeEx nex = (INodeEx) node;
        char c = nex.getAccessKey();
        
        return c == key;
    }
    
    @Override
    public boolean isReachable(IEvalTarget evalTarget, Node target) {
        if (!(evalTarget instanceof Node))
            return false;
        
        Node node = (Node) evalTarget;
        
        Node a1 = searchNonReducibleAncestor(node);
        if (a1 == null)
            return false;
        Node a2 = searchNonReducibleAncestor(target);
        if (a2 == null)
            return false;
        
        
        if (a1.getParentNode() == null)
            return false;
        if (a2.getParentNode() == null)
            return false;
        
        
        if (!(a1.getParentNode().isSameNode(a2.getParentNode()))) {
            return false;
        } else {
            if (a1 instanceof IEvalTarget && a2 instanceof IEvalTarget) {
                IEvalTarget t1 = (IEvalTarget) a1;
                IEvalTarget t2 = (IEvalTarget) a2;
                if (isLink(t1) && isLink(t2)) {
                    return true;
                }
                if (isButton(t1) && isButton(t2)) {
                    return true;
                }
            }
        }
        
        
        Node start = node;

        Node end = target;
        if (end instanceof Text)
            end = getPrev(end);
        if (end == null)
            return false;
        
        Node next = getNext(start);
        while (next != null) {
            if (!(next instanceof IEvalTarget)) {
                next = getNext(next);
                continue;
            }
            if ((!Vocabulary.isReducible().eval((IEvalTarget) next)))
                return false;
            
            if(next.isSameNode(end))
                break;
            next = getNext(next);
        } 
        
        return true;
    }
   
    private Node getPrev(Node node) {
        Node prev = node.getPreviousSibling();
        if (prev != null)
            return prev;
        return node.getParentNode();
    }
    
    private Node getNext(Node node) {
        Node next = node.getNextSibling();
        if (next != null)
            return next;
        return node.getParentNode();
    }

    private Node searchNonReducibleAncestor(Node node) {
        Node child = null;
        Node parent = node;
        
        while (parent != null && parent instanceof IEvalTarget && 
                (parent instanceof Text 
                        || isImage((IEvalTarget) parent))
                        || isReducible((IEvalTarget) parent)) {
            child = parent;
            parent = parent.getParentNode();
        }

        return child;
    }
}
